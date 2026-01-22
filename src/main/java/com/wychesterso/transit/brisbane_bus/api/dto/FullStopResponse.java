package com.wychesterso.transit.brisbane_bus.api.dto;

import java.util.List;

public record FullStopResponse(
        BriefStopResponse stopInfo,
        List<BriefServiceResponse> services
) {}
