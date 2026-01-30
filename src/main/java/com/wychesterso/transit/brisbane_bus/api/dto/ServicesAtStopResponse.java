package com.wychesterso.transit.brisbane_bus.api.dto;

public record ServicesAtStopResponse(
        String routeShortName,
        String routeLongName,
        String tripHeadsign,
        Integer directionId,
        String routeColor,
        String routeTextColor,
        String stopId,
        Double stopLat,
        Double stopLon
) {}
