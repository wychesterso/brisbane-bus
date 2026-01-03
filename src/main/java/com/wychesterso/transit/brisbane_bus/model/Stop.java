package com.wychesterso.transit.brisbane_bus.model;

public record Stop(
        String stopId,
        String stopCode,
        String stopName,
        String stopDesc,
        Double stopLat,
        Double stopLon,
        String zoneId,
        String stopUrl,
        Integer locationType,
        String parentStation,
        String platformCode
) {}