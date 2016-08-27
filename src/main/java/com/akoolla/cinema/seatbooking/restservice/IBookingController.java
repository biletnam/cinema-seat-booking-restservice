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
}
