package com.wychesterso.transit.brisbane_bus.api.service.cache;

import com.wychesterso.transit.brisbane_bus.api.dto.ServiceGroupAtStopDTO;

import java.util.List;

public record ServiceGroupAtStopList(
        List<ServiceGroupAtStopDTO> serviceGroupAtStopList
) {}
