package com.wychesterso.transit.brisbane_bus.api.cache.dto;

import com.wychesterso.transit.brisbane_bus.api.dto.BriefServiceResponse;

import java.util.List;

public record BriefServiceResponseList(
        List<BriefServiceResponse> briefServiceResponseList
) {}
