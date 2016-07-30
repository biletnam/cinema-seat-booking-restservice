package com.akoolla.cinema.seatbooking.restservice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
public class BookingController implements IBookingController {

	@RequestMapping("/screening/{screeningRef}")
	@ResponseBody
	@Override
	public String getScreening(@RequestParam("json.wrf") String jsonToken, @PathVariable("screeningRef") String screeningRef) throws JsonProcessingException {
		SeatBookingResponse response = new SeatBookingResponse(jsonToken);
		response.addOutput("screening", new IScreeningDto() {
			
			@Override
			public String getFilmTitle() {
				return "What We Do In The Shadows (15)";
			}

			@Override
			public int getConcessionCost() {
				// TODO Auto-generated method stub
				return 100;
			}
			@Override
			public int getWheelChairPrice() {
				// TODO Auto-generated method stub
				return 150;
			}

			@Override
			public int getStandardPrice() {
				// TODO Auto-generated method stub
				return 200;
			}

			@Override
			public int getStandardSpaces() {
				return 20;
			}

			@Override
			public int getWheelChairSpaces() {
				return 4;
			}

			@Override
			public int getConcessionSpaces() {
				return 20;
			}
		});
		
		return response.writeValueAsString();
	}
	
	@RequestMapping("/screening/{screening.ref}/book")
	@ResponseBody
	@Override
	public String makeBooking(@RequestParam("json.wrf") String jsonToken, @PathVariable("screening.ref")  String screeningRef, 
			@RequestParam(name="isMember",required=false) boolean isMember, @RequestParam("standard") String numStandardSeats, 
			@RequestParam("concessions") String numConcessionSeats,
			@RequestParam("wheelchairs") String numOfWheelChairs, @RequestParam("bookedName") String name) throws JsonProcessingException {
		
		// TODO Needs to do real creation of booking etc..
		SeatBookingResponse response = new SeatBookingResponse(jsonToken);
		response.addOutput("bookingRef", "213213213");
		response.addOutput("priceInPence", 1250);
		response.addOutput("screeningName", "What we do in the Shadows");
		response.addOutput("screeningRating", "15");
		response.addOutput("screeningDateAndTime", "Friday, April 1st 8:30pm");
		
		return response.writeValueAsString();
	}
	
	@RequestMapping("/booking/cancel")
	@ResponseBody
	@Override
	public String cancelBooking(@RequestParam("json.wrf") String jsonToken, @RequestParam("ref")  String bookingRef) throws JsonProcessingException{
		if("123".equalsIgnoreCase(bookingRef)){
			SeatBookingResponse response = new SeatBookingResponse(jsonToken);
			response.addOutput("message", "Cancelled");
			
			return response.writeValueAsString();
		} else {
			throw new IllegalArgumentException();
		}
	}
}
