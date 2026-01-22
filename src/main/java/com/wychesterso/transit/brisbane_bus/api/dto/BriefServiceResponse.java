package com.wychesterso.transit.brisbane_bus.api.dto;

public record BriefServiceResponse(
        ServiceId routeGroup,
        String routeShortName,
        String routeLongName,
        ArrivalsAtStopResponse arrivalsAtNearestStop
) {}
