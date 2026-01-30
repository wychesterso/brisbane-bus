package com.wychesterso.transit.brisbane_bus.api.service;

import com.wychesterso.transit.brisbane_bus.api.cache.ServiceGroupCache;
import com.wychesterso.transit.brisbane_bus.api.dto.*;
import com.wychesterso.transit.brisbane_bus.api.cache.dto.ServiceGroupDTO;
import com.wychesterso.transit.brisbane_bus.st.model.ServiceGroupKey;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServiceGroupService {

    private final ServiceGroupCache cache;

    private final ServiceGroupStopLocator serviceGroupStopLocator;
    private final ArrivalsService arrivalsService;

    public ServiceGroupService(
            ServiceGroupCache cache,
            ServiceGroupStopLocator serviceGroupStopLocator,
            ArrivalsService arrivalsService) {
        this.cache = cache;
        this.serviceGroupStopLocator = serviceGroupStopLocator;
        this.arrivalsService = arrivalsService;
    }

    public List<BriefServiceResponse> getServicesByPrefix(String prefix, double lat, double lon) {
        if (prefix == null || prefix.isBlank()) return List.of();

        List<ServiceGroupDTO> serviceGroups = cache.getServicesByPrefix(prefix);

        return serviceGroups.stream()
                .map(sg -> toResponseWithAdjacent(sg, lat, lon))
                .toList();
    }

    public List<BriefServiceResponse> getServicesAtStop(String stopId) {
        if (stopId == null || stopId.isBlank()) return List.of();

        List<ServiceGroupAtStopDTO> serviceGroups = cache.getServicesAtStop(stopId);

        return serviceGroups.stream()
                .map(sg -> toResponseWithStop(sg, stopId))
                .toList();
    }

    public List<BriefServiceResponse> getServicesAtStops(List<String> stopIds, double lat, double lon) {
        if (stopIds == null || stopIds.isEmpty()) return List.of();

        record Candidate(
                ServiceGroupAtStopDTO serviceGroup,
                double distance
        ) {}

        Map<ServiceGroupKey, Candidate> closestPerService = new HashMap<>();

        for (String stopId : new HashSet<>(stopIds)) {
            List<ServiceGroupAtStopDTO> servicesAtStop = cache.getServicesAtStop(stopId);

            for (ServiceGroupAtStopDTO sg : servicesAtStop) {
                double distance = squaredDistance(
                        lat,
                        lon,
                        sg.stopLat(),
                        sg.stopLon()
                );

                ServiceGroupKey key = new ServiceGroupKey(
                        sg.routeShortName(),
                        sg.tripHeadsign(),
                        sg.directionId()
                );

                closestPerService.merge(
                        key,
                        new Candidate(sg, distance),
                        (a, b) -> a.distance <= b.distance ? a : b
                );
            }
        }

        return closestPerService.values().stream()
                .sorted(
                        Comparator
                                .comparingDouble(Candidate::distance)
                                .thenComparing(cd -> cd.serviceGroup.routeShortName())
                )
                .map(cd -> toResponseWithStop(
                        cd.serviceGroup,
                        cd.serviceGroup.stopId()
                ))
                .toList();
    }

    private BriefServiceResponse toResponseWithAdjacent(
            ServiceGroupDTO dto,
            double lat,
            double lon) {

        BriefStopResponse adjacentStop = serviceGroupStopLocator.getAdjacentStopForService(
                dto.routeShortName(),
                dto.tripHeadsign(),
                dto.directionId(),
                lat,
                lon
        );
        ArrivalsAtStopResponse arrivalsAtStopResponse = arrivalsService.getNextArrivalsForServiceAtStop(
                adjacentStop.stopId(),
                dto.routeShortName(),
                dto.tripHeadsign(),
                dto.directionId()
        );

        return new BriefServiceResponse(
                new ServiceId(
                        dto.routeShortName(),
                        dto.tripHeadsign(),
                        dto.directionId()
                ),
                dto.routeShortName(),
                dto.routeLongName(),
                arrivalsAtStopResponse
        );
    }

    private BriefServiceResponse toResponseWithStop(
            ServiceGroupAtStopDTO dto,
            String stopId) {

        ArrivalsAtStopResponse arrivalsAtStopResponse = arrivalsService.getNextArrivalsForServiceAtStop(
                stopId,
                dto.routeShortName(),
                dto.tripHeadsign(),
                dto.directionId()
        );

        return new BriefServiceResponse(
                new ServiceId(
                        dto.routeShortName(),
                        dto.tripHeadsign(),
                        dto.directionId()
                ),
                dto.routeShortName(),
                dto.routeLongName(),
                arrivalsAtStopResponse
        );
    }

    private double squaredDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {
        return Math.pow(lat1 - lat2, 2) + Math.pow(lon1 - lon2, 2);
    }
}
