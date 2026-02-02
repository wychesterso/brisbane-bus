package com.wychesterso.transit.brisbane_bus.api.controller.dto;

public record ServiceResponse(
        ServiceGroup serviceGroup,
        String routeShortName,
        String routeLongName,
        ArrivalsAtStopResponse arrivalsAtNearestStop
) {}
