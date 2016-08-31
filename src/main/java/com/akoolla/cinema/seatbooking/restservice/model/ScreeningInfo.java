package com.akoolla.cinema.seatbooking.restservice.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.akoolla.cinema.seatbooking.core.IScreening;
import com.akoolla.cinema.seatbooking.core.IScreening.SEAT_TYPE;
import com.akoolla.cinema.seatbooking.core.film.IFilm;

/**
 * Model portion of the MVC element of a screening
 * @author richardTiffin
 */
public class ScreeningInfo implements Comparable<ScreeningInfo>{
    private final String ref;
    private final String dateTime;
    private final String filmTitle;
    private final String rating;
    private final String releaseDate;
    private final String releaseCountry;
    private final int filmLength;
    private final String description;
    private final int availableSeats;
    private final int availableWheelChairSpaces;
    private final DateTime screeningTime;

    public ScreeningInfo(IScreening screening){
        this.dateTime = formatScreeningDateTime(screening.getScreeningTime());
        IFilm film = screening.getFilm();
        
        if(film == null){
          throw new IllegalArgumentException("Screening has no film");  
        } else {
            this.ref = screening.get_id();
            this.filmTitle = film.getName();
            this.rating = film.getRating();
            this.releaseDate = "" + film.getReleaseDate().getYear();
            this.filmLength = film.getLengthInMins();
            this.releaseCountry =  film.getCountry();
            this.description = film.getDescription();
            this.availableSeats = screening.getNumberOfBookableSeats(SEAT_TYPE.STANDARD) - screening.getNumberOfBookedSeats(SEAT_TYPE.STANDARD);
            this.availableWheelChairSpaces = screening.getNumberOfBookableSeats(SEAT_TYPE.WHEELCHAIR) - screening.getNumberOfBookedSeats(SEAT_TYPE.WHEELCHAIR);
            this.screeningTime = screening.getScreeningTime();
        } 
    }

    public String getDateTime(){
        return dateTime;
    }
    
    public String getFilmTitle(){
        return filmTitle;
    }
    
    public String getRating(){
        return rating;
    }
    
    public String getReleaseDate(){
        return releaseDate;
    }
    
    public String getReleaseCountry(){
        return releaseCountry;
    }
    
    public int getFilmLengthInMins(){
        return filmLength;
    }
    
    public String getDescription(){
        return description;
    }
   
    public String getRef() {
        return ref;
    }
    
    
    /**
     * @param screeningTime
     * @return Formats date time into string similar to Thursday, Sept.15th 8pm & Friday, Sept. 16th 8:30pm.
     */
    private String formatScreeningDateTime(DateTime screeningTime){ //TODO Format!
        DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, MMMM.dd K:mma");
        return screeningTime.toString(fmt);
    }

    /**
     * Return the availableSeats.
     *
     * @return the availableSeats
     */
    public int getAvailableSeats() {
        return availableSeats;
    }

    /**
     * Return the availableWheelChairSpaces.
     *
     * @return the availableWheelChairSpaces
     */
    public int getAvailableWheelChairSpaces() {
        return availableWheelChairSpaces;
    }

    /**
     * TODO.
     * 
     * @return
     */
    public boolean isFullyBooked() {
        return availableSeats == 0 && availableWheelChairSpaces == 0;
    }

    /**
     * Return the screeningTime.
     *
     * @return the screeningTime
     */
    protected DateTime getScreeningTime() {
        return screeningTime;
    }

    /**
     * @param o
     * @return
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ScreeningInfo o) {
        return getScreeningTime().compareTo(o.getScreeningTime());}
}
