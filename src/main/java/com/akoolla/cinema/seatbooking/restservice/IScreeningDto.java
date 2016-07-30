package com.akoolla.cinema.seatbooking.restservice;

public interface IScreeningDto {

	String getFilmTitle();

	/**
	 * @return cost of concession seat in pence
	 */
	int getConcessionCost();

	int getWheelChairPrice();

	int getStandardPrice();

	int getStandardSpaces();
	
	int getWheelChairSpaces();
	
	int getConcessionSpaces();
}
