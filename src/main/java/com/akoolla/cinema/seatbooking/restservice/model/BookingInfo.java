package com.akoolla.cinema.seatbooking.restservice.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.akoolla.cinema.seatbooking.core.IBooking;

/**
 * BookingInfo.
 *
 * @author richardTiffin
 */
public class BookingInfo {
    private String ref;
    private String name;
    private String date;
    private int seats;
    private int wheelchairs;
    
    /**
     * @param booking
     */
    public BookingInfo(IBooking booking) {
        this.ref = booking.getBookingReference().toString();
        this.name = booking.getCustomer().getCustomerName();
        this.date = formatScreeningDateTime(booking.dateOfBooking());
        this.seats = booking.getNumberOfSeats();
        this.wheelchairs = booking.getNumberOfWheelChairs();
    }
    
    
    /**
     * @param screeningTime
     * @return Formats date time into string similar to Thursday, Sept.15th 8pm & Friday, Sept. 16th 8:30pm.
     */
    private String formatScreeningDateTime(DateTime screeningTime){ //TODO Format!
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/mm/yyy");
        return screeningTime.toString(fmt);
    }


    /**
     * Return the ref.
     *
     * @return the ref
     */
    public String getRef() {
        return ref;
    }


    /**
     * Return the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * Return the date.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }


    /**
     * Return the seats.
     *
     * @return the seats
     */
    public int getSeats() {
        return seats;
    }


    /**
     * Return the wheelchairs.
     *
     * @return the wheelchairs
     */
    public int getWheelchairs() {
        return wheelchairs;
    }

}
