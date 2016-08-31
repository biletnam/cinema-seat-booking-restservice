package com.akoolla.cinema.seatbooking.restservice.model;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.joda.time.DateTime;

import com.akoolla.cinema.seatbooking.core.IBooking;
import com.akoolla.cinema.seatbooking.core.ICustomer;
import com.akoolla.cinema.seatbooking.core.impl.BookingReference;

/**
 * ShowBookingsTest.
 *
 * @author richardTiffin
 */
public class ShowBookingsTest {
    @Test
    public void bookingModelCanBeCreateFromBoooking() throws Exception {
        BookingReference bookingRef = new BookingReference("123");
        
        
        ICustomer customer = mock(ICustomer.class);
        when(customer.getCustomerName()).thenReturn("Fred");
        
        IBooking booking = mock(IBooking.class);
        when(booking.getCustomer()).thenReturn(customer);
        when(booking.getBookingReference()).thenReturn(bookingRef);
        when(booking.dateOfBooking()).thenReturn(new DateTime());
        
        
        BookingInfo bookingInfo = new BookingInfo(booking);
        assertEquals(bookingInfo.getName(), "Fred");
        
    }
}
