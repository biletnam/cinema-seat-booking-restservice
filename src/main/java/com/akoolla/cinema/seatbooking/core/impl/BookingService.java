package com.akoolla.cinema.seatbooking.core.impl;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.internal.MongoJackModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.akoolla.cinema.seatbooking.core.IBooking;
import com.akoolla.cinema.seatbooking.core.IBookingRequest;
import com.akoolla.cinema.seatbooking.core.IBookingService;
import com.akoolla.cinema.seatbooking.core.IScreening;
import com.akoolla.cinema.seatbooking.core.ScreeningIsFullyBookedException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mongodb.DB;
import com.mongodb.MongoClient;


@Service
public class BookingService implements IBookingService {
	private static final String SCREENINGS = "screenings";	
	
	private final MongoClient mongoClient;
	private final DB db;
	
	private final ObjectMapper mapper;
	private final JacksonDBCollection<Screening, String> screeningCollection;
	
	@SuppressWarnings("deprecation")
    public BookingService(
	        @Value("${mongo.host}") String mongoHost,
	        @Value("${mongo.db.name}") String dbName,
	        @Value("${mongo.db.user}") String dbUser,
	        @Value("${mongo.db.password}") String dbPass) throws UnknownHostException {
	    
		 mongoClient = new MongoClient(mongoHost);
		 db = mongoClient.getDB(dbName);
		 
         mapper = new ObjectMapper();
         MongoJackModule.configure(mapper);
         mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         mapper.registerModule(new JodaModule());
         mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
         
         screeningCollection = JacksonDBCollection.wrap(db.getCollection(SCREENINGS), Screening.class,
                 String.class, mapper);
	}
	

	@Override
	public Set<IScreening> findAllScreenings() {
	    DBCursor<Screening> cursor = screeningCollection.find();

        Set<IScreening> screenings = new HashSet<>();
        for (Screening screening : cursor) {
            screenings.add(screening);
        }

        return screenings;
	}

	@Override
	public List<IBooking> listBookings(String screeningId) {
	    throw new UnsupportedOperationException();
	}

	@Override
	public void createScreening(IScreening screening) throws IllegalArgumentException {
	    screeningCollection.insert((Screening) screening);
	}

	@Override
	public IBooking updateScreening(String screeningId, IBookingRequest bookingRequest)
			throws ScreeningIsFullyBookedException {
	    IScreening screening = screeningCollection.findOneById(screeningId);
        
        IBooking booking = null;
        if(screening != null ){
            booking = screening.createBooking(bookingRequest);
            screeningCollection.updateById(screening.get_id(),(Screening)screening);
        }

        return booking;
	}


    /**
     * @param arg0
     * @return
     * @see com.akoolla.cinema.seatbooking.core.IBookingService#findScreening(java.lang.String)
     */
    @Override
    public IScreening findScreening(String screeningId) {
        return screeningCollection.findOneById(screeningId);
    }


    /**
     * Return the db.
     *
     * @return the db
     */
    protected DB getDb() {
        return db;
    }


    /**
     * @param arg0
     * @see com.akoolla.cinema.seatbooking.core.IBookingService#cancelABooking(com.akoolla.cinema.seatbooking.core.IBooking)
     */
    @Override
    public void cancelABooking(IBooking arg0) {
        throw new UnsupportedOperationException();
    }
}
