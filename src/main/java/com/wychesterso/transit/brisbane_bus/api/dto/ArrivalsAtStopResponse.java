package com.wychesterso.transit.brisbane_bus.api.dto;

import java.util.List;

public record ArrivalsAtStopResponse(
        BriefStopResponse stop,
        Integer stopSequence,
        List<ArrivalResponse> nextThreeArrivals
) {}
