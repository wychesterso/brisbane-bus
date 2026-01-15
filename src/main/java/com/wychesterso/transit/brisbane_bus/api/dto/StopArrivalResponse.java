package com.wychesterso.transit.brisbane_bus.api.dto;

public record StopArrivalResponse(
        String tripId,
        int arrivalTimeSeconds,
        String arrivalTimeLocal,
        int departureTimeSeconds,
        String departureTimeLocal
) {}