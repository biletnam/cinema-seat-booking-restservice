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

	String makeBooking(String jsonToken, String screeningRef, boolean isMember, String numStandardSeats,
			String numConcessionSeats, String numOfWheelChairs, String name) throws JsonProcessingException;

	String cancelBooking(String jsonToken, String bookingRef) throws JsonProcessingException;
}
