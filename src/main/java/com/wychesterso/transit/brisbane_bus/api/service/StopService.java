package com.wychesterso.transit.brisbane_bus.api.service;

import com.wychesterso.transit.brisbane_bus.api.dto.BriefStopResponse;
import com.wychesterso.transit.brisbane_bus.st.model.Stop;
import com.wychesterso.transit.brisbane_bus.st.repository.StopRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StopService {

    private static final double LATDELTA = 0.009;

    private final StopRepository repository;

    public StopService(StopRepository repository) {
        this.repository = repository;
    }

    public BriefStopResponse getStopInfo(String stopId) {
        return toResponse(repository.findStopById(stopId));
    }

    public List<BriefStopResponse> getAdjacentStops(Double lat, Double lon) {
        double lonDelta = LATDELTA / Math.cos(Math.toRadians(lat));

        List<Stop> adjacentStops = repository.findAdjacentStops(
                lat,
                lon,
                lat - LATDELTA,
                lon - lonDelta,
                lat + LATDELTA,
                lon + lonDelta
        );

        return adjacentStops.stream().map(this::toResponse).toList();
    }

    public BriefStopResponse getAdjacentStopForService(
            String routeShortName,
            String tripHeadsign,
            int directionId,
            Double lat,
            Double lon) {
        double lonDelta = LATDELTA / Math.cos(Math.toRadians(lat));

        return toResponse(repository.findMostAdjacentStopForService(
                routeShortName,
                tripHeadsign,
                directionId,
                lat,
                lon,
                lat - LATDELTA,
                lon - lonDelta,
                lat + LATDELTA,
                lon + lonDelta
        ));
    }

    private BriefStopResponse toResponse(Stop s) {
        return new BriefStopResponse(
                s.getStopId(),
                s.getStopCode(),
                s.getStopName(),
                s.getStopLat(),
                s.getStopLon(),
                s.getZoneId()
        );
    }
}
