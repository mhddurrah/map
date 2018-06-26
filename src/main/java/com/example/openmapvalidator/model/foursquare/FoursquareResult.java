package com.example.openmapvalidator.model.foursquare;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FoursquareResult {
    private Response response;
    @JsonIgnore
    private Meta meta;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
