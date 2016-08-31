package com.akoolla.cinema.seatbooking.restservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.akoolla.cinema.seatbooking.core.IBooking;
import com.akoolla.cinema.seatbooking.core.IBookingRequest;
import com.akoolla.cinema.seatbooking.core.IBookingService;
import com.akoolla.cinema.seatbooking.core.IScreening;
import com.akoolla.cinema.seatbooking.core.ScreeningIsFullyBookedException;
import com.akoolla.cinema.seatbooking.core.film.Film;
import com.akoolla.cinema.seatbooking.core.impl.BookingRequest;
import com.akoolla.cinema.seatbooking.core.impl.Screening;
import com.akoolla.cinema.seatbooking.restservice.model.BookingInfo;
import com.akoolla.cinema.seatbooking.restservice.model.ScreeningInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
public class BookingController implements IBookingController {

    // TODO: Need to wire one of these up via something like mongo
    private IBookingService bookingService;

    public BookingController() {
    }

    @Autowired
    public BookingController(IBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @RequestMapping("/screening/")
    @ResponseBody
    @Override
    public String listScreenings(@RequestParam("json.wrf") String jsonToken) throws JsonProcessingException {
        SeatBookingResponse response = new SeatBookingResponse(jsonToken);

        Set<ScreeningInfo> screenings = new TreeSet<>();
        bookingService.findAllScreenings().forEach(s -> {
            screenings.add(new ScreeningInfo(s));
        });
        response.addOutput("screenings", screenings);

        return response.writeValueAsString();
    }

    @RequestMapping("/screening/{screeningRef}")
    @ResponseBody
    @Override
    public String getScreening(@RequestParam("json.wrf") String jsonToken,
            @PathVariable("screeningRef") String screeningRef) throws JsonProcessingException {
        IScreening screening = bookingService.findScreening(screeningRef);

        SeatBookingResponse response = new SeatBookingResponse(jsonToken);
        response.addOutput("screening", new ScreeningInfo(screening));

        return response.writeValueAsString();
    }

    @RequestMapping("/screening/{screeningRef}/bookings")
    @ResponseBody
    @Override
    public String getBookings(@RequestParam("json.wrf") String jsonToken,
            @PathVariable("screeningRef") String screeningRef) throws JsonProcessingException {
        IScreening screening = bookingService.findScreening(screeningRef);
        if (screening == null) {
            throw new IllegalArgumentException("Screening not found");
        } else {
            SeatBookingResponse response = new SeatBookingResponse(jsonToken);
            
            //TODO: Order by booking date
            List<BookingInfo> bookings = new ArrayList<>();
            screening.listBookings().forEach(b -> {
                bookings.add(new BookingInfo(b));
                
            });
            
            response.addOutput("bookings", bookings);
            return response.writeValueAsString();
        }
    }

    @RequestMapping("/screening/{screening.ref}/book")
    @ResponseBody
    @Override
    public String makeBooking(
            @RequestParam("json.wrf") String jsonToken,
            @PathVariable("screening.ref") String screeningRef,
            @RequestParam(value = "standard", defaultValue = "0") int numStandardSeats,
            @RequestParam(value = "wheelchairs", defaultValue = "0") int numOfWheelChairs,
            @RequestParam("bookedName") String name,
            @RequestParam("email") String email) throws JsonProcessingException {

        IBookingRequest bookingRequest = new BookingRequest(name, email, numStandardSeats, numOfWheelChairs);
        IScreening screening = bookingService.findScreening(screeningRef);
        ScreeningInfo info = new ScreeningInfo(screening);

        try {
            IBooking booking = bookingService.updateScreening(screeningRef, bookingRequest);

            SeatBookingResponse response = new SeatBookingResponse(jsonToken);
            response.addOutput("bookingRef", booking.getBookingReference().toString());
            response.addOutput("screeningName", info.getFilmTitle());
            response.addOutput("screeningRating", info.getRating());
            response.addOutput("screeningDateAndTime", info.getDateTime());
            response.addOutput("seatsBooked", booking.getNumberOfSeats());
            response.addOutput("wheelChairsBooked", booking.getNumberOfWheelChairs());

            return response.writeValueAsString();

        } catch (ScreeningIsFullyBookedException e) {
            // TODO: Send REST error http status - could do something with the reponse too...
        }

        return ""; // TODO
    }
    
    @RequestMapping("/booking/cancel")
    @ResponseBody
    @Override
    public String cancelBooking(@RequestParam("json.wrf") String jsonToken, @RequestParam("ref") String bookingRef)
            throws JsonProcessingException {

        try {
            bookingService.cancelABooking(bookingRef);

            SeatBookingResponse response = new SeatBookingResponse(jsonToken);
            response.addOutput("message", "Cancelled");

            return response.writeValueAsString();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }
    }

    @PostConstruct
    public void setUpDemo() {
        // TODO: Just for testing purposes...
        DateTime screeningTime = new DateTime(2016, 9, 15, 20, 0, DateTimeZone.UTC);
        Film film = new Film(UUID.randomUUID().toString(), 
                "Hail Ceaser", 
                "", 
                "12A", 
                new DateTime(2016, 3, 4, 0, 0), 106,
                "USA");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 9, 23, 20, 30, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "Anomalisa", 
                "", 
                "15", 
                new DateTime(2016, 3, 4, 0, 0), 90,
                "USA");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 9, 30, 20, 30, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "Florence Foster Jenkins", 
                "", 
                "PG", 
                new DateTime(2016, 3, 4, 0, 0), 108,
                "UK");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 10, 2, 15, 00, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "Wear Your Pants with Pride", 
                "", 
                "Suitable for all ages", 
                new DateTime(2016, 3, 4, 0, 0), 60,
                "Nutty Noah");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 10, 7, 20, 30, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "The Assassin", 
                "", 
                "12A", 
                new DateTime(2015, 3, 4, 0, 0), 106,
                "Taiwan");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 10, 8, 19, 30, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "Northern Soul Night in Axbridge Town Hall", 
                "", 
                "", 
                new DateTime(2016, 3, 4, 0, 0), 210,
                "");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 10, 14, 20, 30, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "Victoria", 
                "", 
                "15", 
                new DateTime(2015, 3, 4, 0, 0), 135,
                "Germany");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 10, 20, 20, 00, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "High Rise", 
                "", 
                "PG", 
                new DateTime(2015, 3, 4, 0, 0), 106,
                "UK");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 10, 21, 20, 30, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "High Rise", 
                "", 
                "PG", 
                new DateTime(2015, 3, 4, 0, 0), 106,
                "UK");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 10, 28, 20, 30, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "The Ecstasy of Wilko Johnson", 
                "", 
                "15", 
                new DateTime(2015, 3, 4, 0, 0), 92,
                "UK");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 11, 4, 20, 30, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "You’ve Been Trumped", 
                "", 
                "PG", 
                new DateTime(2012, 3, 4, 0, 0), 101,
                "UK");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 11, 11, 20, 30, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "The Jungle Book", 
                "", 
                "PG", 
                new DateTime(2016, 3, 4, 0, 0), 104,
                "USA/UK");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 11, 17, 20, 00, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "Love and Friendship", 
                "", 
                "U", 
                new DateTime(2016, 3, 4, 0, 0), 92,
                "UK");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 11, 18, 20, 30, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(),
                "Love and Friendship", 
                "", 
                "U", 
                new DateTime(2016, 3, 4, 0, 0), 92,
                "UK");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 11, 25, 20, 30, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "Room", 
                "", 
                "15", 
                new DateTime(2016, 3, 4, 0, 0), 118,
                "USA");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 12, 2, 20, 30, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "The Revenant", 
                "", 
                "PG", 
                new DateTime(2016, 3, 4, 0, 0), 153,
                "USA");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 12, 8, 20, 00, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "Eddie the Eagle", 
                "", 
                "PG", 
                new DateTime(2016, 3, 4, 0, 0), 103,
                "UK");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        screeningTime = new DateTime(2016, 12, 9, 20, 30, DateTimeZone.UTC);
        film = new Film(UUID.randomUUID().toString(), 
                "Eddie the Eagle", 
                "", 
                "PG", 
                new DateTime(2016, 3, 4, 0, 0), 103,
                "UK");
        bookingService.createScreening(new Screening(screeningTime, film, 32, 2));
        
        
    }

    @PreDestroy
    public void clearDemo() throws Exception {
        ((BookingService) bookingService).getDb().dropDatabase();
    }
}
