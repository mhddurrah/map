package com.example.openmapvalidator.helper;

import java.util.HashMap;
import java.util.Map;

public class PlaceTypeMapper {

    /**
     * key : openstreet place type
     * value : google place type
     */
    private Map<String, String> openToGoogle;

    public PlaceTypeMapper() {
        openToGoogle = new HashMap<>();
        fillTypeMap();
    }

    public void fillTypeMap() {
        openToGoogle.put("fuel", "gas_station");
        openToGoogle.put("bakery", "bakery");
        openToGoogle.put("parking_entrance", "parking");
        openToGoogle.put("parking", "parking");
        openToGoogle.put("pharmacy", "pharmacy");
        openToGoogle.put("pub", "cafe");
        openToGoogle.put("restaurant", "restaurant");
        openToGoogle.put("car_sharing", "car_sharing");
        openToGoogle.put("doctors", "doctor");
        openToGoogle.put("supermarket", "supermarket");
        openToGoogle.put("kiosk", "kiosk");
        openToGoogle.put("laundry", "laundry");
        openToGoogle.put("locksmith", "locksmith");
        openToGoogle.put("nightclub", "night_club");
    }

    public Map<String, String> getOpenToGoogle() {
        return openToGoogle;
    }

    public void setOpenToGoogle(Map<String, String> openToGoogle) {
        this.openToGoogle = openToGoogle;
    }
}
