package com.wychesterso.transit.brisbane_bus.model;

public record Route(
        String routeId,
        String routeShortName,
        String routeLongName,
        String routeDesc,
        Integer routeType,
        String routeUrl,
        String routeColor,
        String routeTextColor
) {}