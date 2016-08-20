package com.akoolla.cinema.seatbooking.core.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.akoolla.cinema.seatbooking.core.IBookingService;
import com.akoolla.cinema.seatbooking.core.IScreening;
import com.akoolla.cinema.seatbooking.core.ScreeningIsFullyBookedException;
import com.akoolla.cinema.seatbooking.core.film.Film;

/**
 * ScreeningPersistenceTest.
 *
 * @author richardTiffin
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class ScreeningPersistenceTest {

    @Autowired
    IBookingService bookingService;
    
    @Test
    public void ShouldBeAbleToPersistANewScreening() throws ScreeningIsFullyBookedException {
        DateTime bookingDate = DateTime.now();
        String reference = "awdadawd";

        IScreening screening = new Screening(bookingDate, new Film(reference, "Top Gun", "It's cracking", "PG"), 23);

        try {
            screening.createBooking(new BookingRequest("customer", "23213", 1, 1, 0));
        } catch (ScreeningIsFullyBookedException e) {
            e.printStackTrace();
        }
                
        bookingService.createScreening(screening);
        
        Set<IScreening> screenings = bookingService.findAllScreenings();
        assertNotNull("Screening list returned", screenings);
        assertEquals("Number of screenings", Integer.valueOf(1), Integer.valueOf(screenings.size()));
        
        for(IScreening persistedScreening : screenings){
            screening = persistedScreening;
            assertNotNull("ID", persistedScreening.get_id());
            assertNotNull("Film", persistedScreening.getFilm());
            assertEquals("Top Gun", persistedScreening.getFilm().getName());
            //assertEquals(reference, persistedScreening.getFilm().getUniqueReference());
            assertNotNull("Bookings", persistedScreening.listBookings());
            assertNotNull("Expected booking", persistedScreening.listBookings().get(0));
            assertNotNull("Booking Customer", persistedScreening.listBookings().get(0).getCustomer());
        }
    }
    
//    @Test
//    public void ShouldBeAbleToMakeANewBookingOnAPersistedScreening() throws Exception {
//        DateTime bookingDate = new DateTime(2012, 1, 1, 1, 0);
//        String reference = "awdadawd";
//
//        IScreening screening = new Screening(bookingDate,new Film(reference, "Top Gun", ""), 23);
//        bookingService.createScreening(screening);
//        
//        screening = bookingService.findScreening(reference);
//        
//        IBooking booking = bookingService.updateScreening(screening.get_id(), new BookingRequest("", "", 23));
//        assertNotNull(booking);
//    }
//    
//    @After
//    public void clearDownCollections(){
//        ((BookingService)bookingService).getDb().dropDatabase();
//    }
}
