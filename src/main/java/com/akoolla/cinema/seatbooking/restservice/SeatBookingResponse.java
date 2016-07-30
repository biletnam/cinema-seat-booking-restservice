package com.akoolla.cinema.seatbooking.restservice;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

public class SeatBookingResponse extends ObjectMapper {

	private static final long serialVersionUID = 1L;
	private String jsonToken;
	 private final Map<String, Object> values = new HashMap<String, Object>();
	
	public SeatBookingResponse(final String jsonToken){
		this.jsonToken = jsonToken;
	}
	
    public Object addOutput(final String key, final Object output){
        return values.put(key, output);
    }
    
    /**
     * @param value
     * @return
     * @throws JsonProcessingException
     * @see com.fasterxml.jackson.databind.ObjectMapper#writeValueAsString(java.lang.Object)
     */
    @Override
    public String writeValueAsString(Object value) throws JsonProcessingException {
        return super.writeValueAsString(value);
    }
    
    public String writeValueAsString() throws JsonProcessingException{
        return super.writeValueAsString(new JSONPObject(jsonToken, values));
    }
}
