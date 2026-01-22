package com.wychesterso.transit.brisbane_bus.api.dto;

public record BriefStopResponse(
        String stopId,
        String stopCode,
        String stopName,
        Double stopLat,
        Double stopLon,
        String zoneId
) {}
