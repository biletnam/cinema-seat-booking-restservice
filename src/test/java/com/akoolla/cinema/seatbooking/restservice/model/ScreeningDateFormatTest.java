package com.akoolla.cinema.seatbooking.restservice.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.akoolla.cinema.seatbooking.core.IScreening;
import com.akoolla.cinema.seatbooking.core.film.IFilm;

/**
 * ScreeningDateFormatTest.
 *
 * @author richardTiffin
 */
public class ScreeningDateFormatTest {

    @Test
    public void dateIsCorrectlyFormattedInString() throws Exception {
        DateTime screeningTime = new DateTime(2016, 9, 15, 20, 0, DateTimeZone.getDefault());
        
        IFilm film = mock(IFilm.class);
        when(film.getReleaseDate()).thenReturn(new DateTime());
        
        IScreening screening = mock(IScreening.class);
        when(screening.getFilm()).thenReturn(film);
        when(screening.getScreeningTime()).thenReturn(screeningTime);
        
        
        ScreeningInfo info = new ScreeningInfo(screening);
        assertEquals("Thursday, September.15 8PM", info.getDateTime());
    }
}
