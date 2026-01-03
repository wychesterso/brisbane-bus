package com.wychesterso.transit.brisbane_bus.model;

public record Trip(
        String tripId,
        String routeId,
        String serviceId,
        String tripHeadsign,
        Integer directionId,
        String blockId,
        String shapeId
) {}