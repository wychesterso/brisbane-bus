package com.wychesterso.transit.brisbane_bus.st.model;

public record ServiceGroupKey(
        String routeShortName,
        String tripHeadsign,
        Integer directionId
) {}
