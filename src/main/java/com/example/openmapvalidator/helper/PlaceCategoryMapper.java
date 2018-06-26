package com.example.openmapvalidator.helper;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class PlaceCategoryMapper {

    /**
     * key : openstreet place category
     * value : map < k:google  v:placeCategory, k:foursquare, v:placeCategory >
     */
    private static Map<String, Map<String, String>> openToOtherMaps;

    static {
        openToOtherMaps = new HashMap<>();
        fillTypeMap();
    }

    public static void fillTypeMap() {
        openToOtherMaps.put("fuel", ImmutableMap.of("google", "gas_station", "foursquare", "4bf58dd8d48988d113951735"));
        openToOtherMaps.put("florist", ImmutableMap.of("google", "florist", "foursquare", "4bf58dd8d48988d11b951735"));
        openToOtherMaps.put("dentist", ImmutableMap.of("google", "dentist", "foursquare", "4bf58dd8d48988d178941735"));
        openToOtherMaps.put("butcher", ImmutableMap.of("google", "butcher_shop_deli|butcher", "foursquare", "4bf58dd8d48988d11d951735"));
        openToOtherMaps.put("hairdresser", ImmutableMap.of("google", "hairdresser|hair_salon", "foursquare", ""));
        openToOtherMaps.put("sports", ImmutableMap.of("google", "running_store|store|sports", "foursquare", "4bf58dd8d48988d1f2941735"));
        openToOtherMaps.put("copyshop", ImmutableMap.of("google", "copy_shop", "foursquare", ""));
        openToOtherMaps.put("department_store", ImmutableMap.of("google", "shopping_mall", "foursquare", "4bf58dd8d48988d1fd941735"));
        openToOtherMaps.put("shoes", ImmutableMap.of("google", "shoe_store", "foursquare", "4bf58dd8d48988d107951735"));
        openToOtherMaps.put("boutique", ImmutableMap.of("google", "boutique", "foursquare", "4bf58dd8d48988d104951735"));
        openToOtherMaps.put("house_hold", ImmutableMap.of("google", "kitchen_supply_store|home_goods_store", "foursquare", "58daa1558bbb0b01f18ec1b4"));
        openToOtherMaps.put("bakery", ImmutableMap.of("google", "bakery", "foursquare", "4bf58dd8d48988d16a941735"));
        openToOtherMaps.put("parking_entrance", ImmutableMap.of("google", "parking", "foursquare", "4c38df4de52ce0d596b336e1"));
        openToOtherMaps.put("parking", ImmutableMap.of("google", "parking", "foursquare", "4c38df4de52ce0d596b336e1"));
        openToOtherMaps.put("pharmacy", ImmutableMap.of("google", "pharmacy", "foursquare", "4bf58dd8d48988d10f951735"));
        openToOtherMaps.put("pub", ImmutableMap.of("google", "bar|cafe", "foursquare", "4bf58dd8d48988d11b941735"));
        openToOtherMaps.put("restaurant", ImmutableMap.of("google", "restaurant", "foursquare", "4d4b7105d754a06374d81259"));
        openToOtherMaps.put("car_sharing", ImmutableMap.of("google", "car_sharing", "foursquare", ""));
        openToOtherMaps.put("doctors", ImmutableMap.of("google", "doctor", "foursquare", "4bf58dd8d48988d177941735"));
        openToOtherMaps.put("supermarket", ImmutableMap.of("google", "supermarket", "foursquare", "52f2ab2ebcbc57f1066b8b46"));
        openToOtherMaps.put("kiosk", ImmutableMap.of("google", "kiosk", "foursquare", ""));
        openToOtherMaps.put("laundry", ImmutableMap.of("google", "laundry", "foursquare", "4bf58dd8d48988d1fc941735"));
        openToOtherMaps.put("locksmith", ImmutableMap.of("google", "locksmith", "foursquare", "52f2ab2ebcbc57f1066b8b1e"));
        openToOtherMaps.put("nightclub", ImmutableMap.of("google", "night_club", "foursquare", "4bf58dd8d48988d11f941735"));
        openToOtherMaps.put("bicycle_rental", ImmutableMap.of("google", "bike_sharing_station", "foursquare", ""));
        openToOtherMaps.put("bar", ImmutableMap.of("google", "bar", "foursquare", "4bf58dd8d48988d116941735"));
        openToOtherMaps.put("arts_centre", ImmutableMap.of("google", "art_gallery|museum", "foursquare", "4bf58dd8d48988d1e2931735")); //TODO art - art gallery and center - museum
        openToOtherMaps.put("art", ImmutableMap.of("google", "art_gallery|museum", "foursquare", "4d4b7104d754a06370d81259"));
        openToOtherMaps.put("post_office", ImmutableMap.of("google", "post_office", "foursquare", "4bf58dd8d48988d172941735"));
        openToOtherMaps.put("bank", ImmutableMap.of("google", "bank", "foursquare", "4bf58dd8d48988d10a951735"));
        openToOtherMaps.put("cafe", ImmutableMap.of("google", "cafe|pool_hall", "foursquare", "4bf58dd8d48988d128941735"));
        openToOtherMaps.put("car", ImmutableMap.of("google", "motor_vehicle_dealer", "foursquare", "4eb1c1623b7b52c0e1adc2ec"));
        openToOtherMaps.put("club", ImmutableMap.of("google", "association_or_organization", "foursquare", "52e81612bcbc57f1066b7a35"));
        openToOtherMaps.put("crypt", ImmutableMap.of("google", "historical_place", "foursquare", "4deefb944765f83613cdba6e"));
        openToOtherMaps.put("fast_food", ImmutableMap.of("google", "fast_food_restaurant", "foursquare", "4bf58dd8d48988d16e941735"));
        openToOtherMaps.put("police", ImmutableMap.of("google", "police", "foursquare", "4bf58dd8d48988d12e941735"));
        openToOtherMaps.put("cinema", ImmutableMap.of("google", "movie_theater", "foursquare", "4bf58dd8d48988d17f941735"));
        openToOtherMaps.put("clothes", ImmutableMap.of("google", "clothing_store", "foursquare", "4bf58dd8d48988d103951735"));
        openToOtherMaps.put("books", ImmutableMap.of("google", "book_store", "foursquare", "4bf58dd8d48988d114951735"));
        openToOtherMaps.put("theater", ImmutableMap.of("google", "theater|performing_arts_theater", "foursquare", "4bf58dd8d48988d137941735"));
        openToOtherMaps.put("furniture", ImmutableMap.of("google", "furniture_store", "foursquare", "4bf58dd8d48988d1f8941735"));
        openToOtherMaps.put("public_building", ImmutableMap.of("google", "synagogue|church|mosque|federal_government_office", "foursquare", "50328a8e91d4c4b30a586d6c"));
        openToOtherMaps.put("school", ImmutableMap.of("google", "school", "foursquare", "4bf58dd8d48988d13b941735"));
        openToOtherMaps.put("jewelry", ImmutableMap.of("google", "jewelry_store", "foursquare", "4bf58dd8d48988d111951735"));
        openToOtherMaps.put("library", ImmutableMap.of("google", "library", "foursquare", "4bf58dd8d48988d12f941735"));
        openToOtherMaps.put("bicycle", ImmutableMap.of("google", "bicycle_store", "foursquare", "4bf58dd8d48988d115951735"));
        openToOtherMaps.put("embassy", ImmutableMap.of("google", "embassy", "foursquare", "4bf58dd8d48988d12c951735"));
        openToOtherMaps.put("clock", ImmutableMap.of("google", "watch_store", "foursquare", "52f2ab2ebcbc57f1066b8b2e"));
        openToOtherMaps.put("gift", ImmutableMap.of("google", "gift_shop", "foursquare", "4bf58dd8d48988d128951735"));
        openToOtherMaps.put("coffee", ImmutableMap.of("google", "home_goods_store|cafe", "foursquare", "4bf58dd8d48988d1e0931735"));
        openToOtherMaps.put("chemist", ImmutableMap.of("google", "drug_store", "foursquare", "5745c2e4498e11e7bccabdbd"));
        openToOtherMaps.put("atm", ImmutableMap.of("google", "atm", "foursquare", "52f2ab2ebcbc57f1066b8b56"));
        openToOtherMaps.put("toys", ImmutableMap.of("google", "toy_store", "foursquare", "4bf58dd8d48988d1f3941735"));
        openToOtherMaps.put("wine", ImmutableMap.of("google", "wine_store", "foursquare", "4bf58dd8d48988d119951735"));
        openToOtherMaps.put("courthouse", ImmutableMap.of("google", "courthouse", "foursquare", "4bf58dd8d48988d12b941735"));
    }

    public static Map<String, Map<String, String>> getOpenToOtherMaps() {
        return openToOtherMaps;
    }

}
