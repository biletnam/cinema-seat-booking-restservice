package com.akoolla.cinema.seatbooking.restservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import com.akoolla.cinema.seatbooking.core.IScreening.SEAT_TYPE;
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
        bookingService.findAvailableScreenings(DateTime.now(DateTimeZone.UTC)).forEach(s -> {
            screenings.add(new ScreeningInfo(s));
        });
        response.addOutput("screenings", screenings);

        return response.writeValueAsString();
    }
    
    @RequestMapping("/screening/all/")
    @ResponseBody
    @Override
    public String listAllScreenings(@RequestParam("json.wrf") String jsonToken) throws JsonProcessingException {
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
            response.addOutput("filmName", screening.getFilm().getName());
            response.addOutput("numOfBookedSeats", screening.getNumberOfBookedSeats(SEAT_TYPE.STANDARD));
            response.addOutput("numOfBookedWheelChairs", screening.getNumberOfBookedSeats(SEAT_TYPE.WHEELCHAIR));
            
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

        IBookingRequest bookingRequest = new BookingRequest(name, email, numStandardSeats, numOfWheelChairs, "TODO");
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
    
    @RequestMapping("/screening/add/")
    @ResponseBody
    @Override
    public String createScreening(@RequestParam("json.wrf") String jsonToken,
            @RequestParam("name") String filmName,
            @RequestParam("date") String screeningTime,
            @RequestParam("rating") String rating,
            @RequestParam("releaseDate") String releaseDate,
            @RequestParam("length") int length,
            @RequestParam("country") String country,
            @RequestParam("maxSeats") int maxSeats,
            @RequestParam("maxWheelChairs") int maxWheelChairs,
            @RequestParam(defaultValue="false",required=false,value="cannotBook") boolean cannotBook, 
            @RequestParam(defaultValue="",required=false,value="filmDescription") String filmDescription) throws JsonProcessingException, IllegalArgumentException, IllegalAccessException{
        
        String dateFormat = "dd-MM-yyyy-H-mm";
        DateTimeFormatter fmt = DateTimeFormat.forPattern(dateFormat).withZoneUTC().withLocale(Locale.UK);
                
        DateTime ofScreening = fmt.parseDateTime(screeningTime);
        DateTime screeningReleaseDate = fmt.parseDateTime(releaseDate);
        
        Film film = new Film(UUID.randomUUID().toString(), 
                filmName, 
                filmDescription, 
                rating, 
                screeningReleaseDate, 
                length, 
                country);
        
        IScreening created =  bookingService.createScreening(new Screening(ofScreening, film, maxSeats, maxWheelChairs, cannotBook));
                
        SeatBookingResponse response = new SeatBookingResponse(jsonToken);
        response.addOutput("message", "Added");
        response.addOutput("screening", created);
        
        return response.writeValueAsString();
    }
}
