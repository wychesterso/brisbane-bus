package com.wychesterso.transit.brisbane_bus.api.dto;

public record ServiceId(
        String routeShortName,
        String tripHeadsign,
        int directionId
) {}
