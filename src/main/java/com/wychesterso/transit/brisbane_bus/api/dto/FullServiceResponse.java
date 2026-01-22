package com.wychesterso.transit.brisbane_bus.api.dto;

import java.util.List;

public record FullServiceResponse(
        ServiceId routeGroup,
        String routeShortName,
        String routeLongName,
        String routeColor,
        String routeTextColor,
        List<ArrivalsAtStopResponse> arrivalsAtStops
) {}
