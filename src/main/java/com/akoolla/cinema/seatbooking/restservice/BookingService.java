package com.akoolla.cinema.seatbooking.restservice;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;
import org.mongojack.internal.MongoJackModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.akoolla.cinema.seatbooking.core.IBooking;
import com.akoolla.cinema.seatbooking.core.IBookingReference;
import com.akoolla.cinema.seatbooking.core.IBookingRequest;
import com.akoolla.cinema.seatbooking.core.IBookingService;
import com.akoolla.cinema.seatbooking.core.IScreening;
import com.akoolla.cinema.seatbooking.core.ScreeningIsFullyBookedException;
import com.akoolla.cinema.seatbooking.core.impl.BookingReference;
import com.akoolla.cinema.seatbooking.core.impl.Screening;
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
	
	@Autowired
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
	public IScreening createScreening(IScreening screening) throws IllegalArgumentException {
	    WriteResult<Screening,String> result = screeningCollection.insert((Screening) screening);
	    
	    if(result != null && result.getWriteResult().getError() != null){
	        return result.getSavedObject();
	    } else {
	        new IllegalAccessException( result.getWriteResult().getError());
	    }
	    
	    return null;
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

    @Override
    public void cancelABooking(String bookingRef) throws IllegalArgumentException {
        //TODO: Use mongo querying to find screening with actual booking...
        IBooking foundBooking = null;
        IScreening foundScreening = null;
        IBookingReference toDelete = new BookingReference(bookingRef);
        
        
        for(IScreening screening : findAllScreenings()){
            if(foundBooking != null) {
                break;
            }
            
            for(IBooking booking : screening.listBookings()){
                if(booking.getBookingReference().toString().equals(toDelete.toString())){
                    foundBooking = booking;
                    foundScreening = screening;
                    break;
                }
            }
        }
        
        if(foundBooking == null){
            throw new UnsupportedOperationException("Could not find booking with ref:"+ bookingRef);
        } else {
            foundScreening.cancelBooking(foundBooking);
            screeningCollection.updateById(foundScreening.get_id(),(Screening)foundScreening);
        }
    }
}
