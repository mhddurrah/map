package com.example.openmapvalidator.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Const {
    public static final String EMPTY = "";
    public static final String OPEN_QUOTE = "\"";
    public static final String CLOSE_QUOTE = "\"";

    public static String OPENSTREET_URI_GET_LONG_WITH_OSM_ID;
    public static String GOOGLE_URI_SEARCH_WITH_LONG;
    public static String GOOGLE_RETRIEVE_WITH_PLACE_ID;
    public static String FOURSQUARE_URI_SEARCH_WITH_LONG;

    public static String OSM_COMMAND;
    public static String OSM_COMMAND_CREATE_OPTION;
    public static String OSM_COMMAND_DATABASE_OPTION;
    public static String OSM_COMMAND_DATABASE_ARGUMENT;

    @Value("${openstreet.getlongwithosmid}")
    public void setOpenstreetUriGetLongWithOsmId(String openWithLong) {
        OPENSTREET_URI_GET_LONG_WITH_OSM_ID = openWithLong;
    }

    @Value("${googlemap.searchplacewithlong}")
    public void setGoogleUriSearchWithLong(String searchWithLong) {
        GOOGLE_URI_SEARCH_WITH_LONG = searchWithLong;
    }

    @Value("${googlemap.retrievewithplaceid}")
    public void setGoogleRetrieveWithPlaceId(String retrieveWithPlaceId) {
        GOOGLE_RETRIEVE_WITH_PLACE_ID = retrieveWithPlaceId;
    }

    @Value("${foursquare.searchplacewithlong}")
    public void setFoursquareUriSearchWithLong(String searchWithLong) {
        FOURSQUARE_URI_SEARCH_WITH_LONG = searchWithLong;
    }

    @Value("${osm.command}")
    public void setOsmCommand(String osmCommand) {
        OSM_COMMAND = osmCommand;
    }

    @Value("${osm.command.create.option}")
    public void setOsmCommandCreateOption(String osmCommandCreateOption) {
        OSM_COMMAND_CREATE_OPTION = osmCommandCreateOption;
    }
    @Value("${osm.command.database.option}")
    public void setOsmCommandDatabaseOption(String osmCommandDatabaseOption) {
        OSM_COMMAND_DATABASE_OPTION = osmCommandDatabaseOption;
    }
    @Value("${osm.command.database.argument}")
    public void setOsmCommandDatabaseArgument(String osmCommandDatabaseArgument) {
        OSM_COMMAND_DATABASE_ARGUMENT = osmCommandDatabaseArgument;
    }
}
