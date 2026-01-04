package com.wychesterso.transit.brisbane_bus.model;

public record StopTime(
        String tripId,
        Integer arrivalTime,
        Integer departureTime,
        String stopId,
        Integer stopSequence,
        Integer pickupType,
        Integer dropoffType
) {}
