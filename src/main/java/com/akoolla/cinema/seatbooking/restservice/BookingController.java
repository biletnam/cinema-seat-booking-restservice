package com.akoolla.cinema.seatbooking.restservice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.akoolla.cinema.seatbooking.core.IBooking;
import com.akoolla.cinema.seatbooking.core.IBookingRequest;
import com.akoolla.cinema.seatbooking.core.IBookingService;
import com.akoolla.cinema.seatbooking.core.IScreening;
import com.akoolla.cinema.seatbooking.core.ScreeningIsFullyBookedException;
import com.akoolla.cinema.seatbooking.core.impl.BookingRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
public class BookingController implements IBookingController {
    
    //TODO: Need to wire one of these up via something like mongo
    private IBookingService bookingService;

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
	public String makeBooking(
	        @RequestParam("json.wrf") String jsonToken, 
	        @PathVariable("screening.ref")  String screeningRef, 
			@RequestParam(name="isMember",required=false) boolean isMember, 
			@RequestParam("standard") int numStandardSeats, 
			@RequestParam("concessions") int numConcessionSeats,
			@RequestParam("wheelchairs") int numOfWheelChairs, 
			@RequestParam("bookedName") String name) throws JsonProcessingException {
	    
	    IBookingRequest bookingRequest = new BookingRequest(name, "TODO", numStandardSeats, numConcessionSeats, numOfWheelChairs);
	    IScreening screening = bookingService.findScreening(screeningRef);
	    
	    try {
            IBooking booking = screening.createBooking(bookingRequest);
            
            // TODO Needs to do real creation of booking etc..
            SeatBookingResponse response = new SeatBookingResponse(jsonToken);
            response.addOutput("bookingRef", booking.getBookingReference().toString());
            response.addOutput("priceInPence", 1250); //TODO: Get booking cost
            response.addOutput("screeningName", screening.getFilm().getName());
            response.addOutput("screeningRating", screening.getFilm().getRating());
            response.addOutput("screeningDateAndTime", screening.getScreeningTime()); //TODO: Nicer formatting of time

            return response.writeValueAsString();
            
        } catch (ScreeningIsFullyBookedException e) {
            //TODO: Send REST error http status - could do something with the reponse too...
        }
	    
	    return ""; //TODO
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
