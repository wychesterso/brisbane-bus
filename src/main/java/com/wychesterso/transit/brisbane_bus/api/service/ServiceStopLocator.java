package com.wychesterso.transit.brisbane_bus.api.service;

import com.wychesterso.transit.brisbane_bus.api.dto.BriefStopResponse;
import org.springframework.stereotype.Service;

@Service
public class ServiceStopLocator {

    private final StopService stopService;

    public ServiceStopLocator(StopService stopService) {
        this.stopService = stopService;
    }

    public BriefStopResponse getAdjacentStopForService(
            String routeShortName,
            String tripHeadsign,
            int directionId,
            Double lat,
            Double lon) {

        return stopService.getAdjacentStopForService(
                routeShortName,
                tripHeadsign,
                directionId,
                lat,
                lon
        );
    }
}
