package com.akoolla.cinema.seatbooking.restservice.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.joda.time.DateTime;
import org.junit.Test;

import com.akoolla.cinema.seatbooking.core.IScreening;
import com.akoolla.cinema.seatbooking.core.IScreening.SEAT_TYPE;
import com.akoolla.cinema.seatbooking.core.film.IFilm;

/**
 * ScreeningIsFullyBookedTest.
 *
 * @author richardTiffin
 */
public class ScreeningIsFullyBookedTest {

    @Test
    public void ifScreeningIsFullyBookedReturnTrueForBookedUpStatus() throws Exception {
        IFilm film = mock(IFilm.class);
        when(film.getReleaseDate()).thenReturn(new DateTime());
                
        IScreening screening = mock(IScreening.class);
        when(screening.getNumberOfBookableSeats(SEAT_TYPE.STANDARD)).thenReturn(0);
        when(screening.getNumberOfBookableSeats(SEAT_TYPE.WHEELCHAIR)).thenReturn(0);
        when(screening.getScreeningTime()).thenReturn(new DateTime());
        when(screening.getFilm()).thenReturn(film);
        
        
        ScreeningInfo info = new ScreeningInfo(screening);
        assertTrue(info.isFullyBooked());
    }
    
    @Test
    public void ifScreeningHasAvailableSeatsThenIsNotFullyBooked() throws Exception {
        IFilm film = mock(IFilm.class);
        when(film.getReleaseDate()).thenReturn(new DateTime());
                
        IScreening screening = mock(IScreening.class);
        when(screening.getNumberOfBookableSeats(SEAT_TYPE.STANDARD)).thenReturn(1);
        when(screening.getNumberOfBookableSeats(SEAT_TYPE.WHEELCHAIR)).thenReturn(0);
        when(screening.getScreeningTime()).thenReturn(new DateTime());
        when(screening.getFilm()).thenReturn(film);
        
        ScreeningInfo info = new ScreeningInfo(screening);
        assertFalse(info.isFullyBooked());
        
        when(screening.getNumberOfBookableSeats(SEAT_TYPE.STANDARD)).thenReturn(0);
        when(screening.getNumberOfBookableSeats(SEAT_TYPE.WHEELCHAIR)).thenReturn(1);
        
        info = new ScreeningInfo(screening);
        assertFalse(info.isFullyBooked());
    }
}
