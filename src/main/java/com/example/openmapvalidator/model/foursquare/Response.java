package com.example.openmapvalidator.model.foursquare;

import java.util.List;

public class Response {
    private List<Venue> venues;

    private boolean confident;

    public boolean isConfident() {
        return confident;
    }

    public void setConfident(boolean confident) {
        this.confident = confident;
    }

    public List<Venue> getVenues() {
        return venues;
    }

    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }
}
