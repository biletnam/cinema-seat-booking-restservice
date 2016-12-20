package com.akoolla.cinema.seatbooking.restservice;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IBookingController {

	/**
	 * @param jsonToken
	 * @param screeningRef
	 * 
	 * @return
	 * @throws JsonProcessingException 
	 */
	String getScreening(String jsonToken, String screeningRef) throws JsonProcessingException;

	String makeBooking(String jsonToken, String screeningRef, int numStandardSeats,
	        int numOfWheelChairs, String name, String email) throws JsonProcessingException;

	String cancelBooking(String jsonToken, String bookingRef) throws JsonProcessingException;

    String listScreenings(String jsonToken) throws JsonProcessingException;
    
    String getBookings(String jsonToken,String screeningRef) throws JsonProcessingException ;

    String listAllScreenings(String jsonToken) throws JsonProcessingException;

    /**
     * TODO.
     * 
     * @param jsonToken
     * @param filmName
     * @param screeningTime
     * @param rating
     * @param releaseDate
     * @param length
     * @param country
     * @param maxSeats
     * @param maxWheelChairs
     * @return
     * @throws JsonProcessingException
     */
    String createScreening(String jsonToken, String filmName, String screeningTime, String rating, String releaseDate,
            int length, String country, int maxSeats, int maxWheelChairs) throws JsonProcessingException;
}
