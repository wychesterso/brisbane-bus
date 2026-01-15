package com.wychesterso.transit.brisbane_bus.api.dto;

public interface StopArrivalDTO {
    String getTripId();
    Integer getArrivalTimeSeconds();
    Integer getDepartureTimeSeconds();
}