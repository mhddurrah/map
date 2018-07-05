package com.example.openmapvalidator.model.foursquare;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class Venue {
    private String id;
    private String name;

    @JsonIgnore
    private VenuePage venuePage;
    @JsonIgnore
    private Location location;
    @JsonIgnore
    private List<Category> categories;
    @JsonIgnore
    private String referralId;
    @JsonIgnore
    private boolean hasPerk;
    @JsonIgnore
    private Delivery delivery;
    @JsonIgnore
    private Events events;

    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }

    public VenuePage getVenuePage() {
        return venuePage;
    }

    public void setVenuePage(VenuePage venuePage) {
        this.venuePage = venuePage;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

    public boolean isHasPerk() {
        return hasPerk;
    }

    public void setHasPerk(boolean hasPerk) {
        this.hasPerk = hasPerk;
    }

    private static class Location {
    }

    private static class Category {
    }

    private static class VenuePage {
    }

    private static class Delivery {
    }

    private static class Events {
    }
}
