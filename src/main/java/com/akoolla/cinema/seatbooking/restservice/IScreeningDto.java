package com.akoolla.cinema.seatbooking.restservice;

public interface IScreeningDto {

	String getFilmTitle();

	int getAvailableStandardSeats();
	
	int getAvailableWheelChairSpaces();
}
