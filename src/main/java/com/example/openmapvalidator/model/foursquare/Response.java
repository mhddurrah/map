package com.example.openmapvalidator.model.foursquare;

import java.util.List;

public class Response {
    private List<Venue> venues;

    public List<Venue> getVenues() {
        return venues;
    }

    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }
}
