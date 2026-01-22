package com.wychesterso.transit.brisbane_bus.api.service;

import com.wychesterso.transit.brisbane_bus.api.dto.BriefStopResponse;
import com.wychesterso.transit.brisbane_bus.api.exception.NotFoundException;
import com.wychesterso.transit.brisbane_bus.st.model.Stop;
import com.wychesterso.transit.brisbane_bus.st.repository.StopRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StopService {

    private final StopRepository repository;

    public StopService(StopRepository repository) {
        this.repository = repository;
    }

    public BriefStopResponse getStop(String stopId) {
        return repository.findStopById(stopId)
                .stream()
                .findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Stop not found: " + stopId));
    }

    public List<BriefStopResponse> getStopsForRoute(String routeId) {
        return repository.findStopsForRoute(routeId)
                .stream()
                .map(this::toResponse)
                .toList();
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
