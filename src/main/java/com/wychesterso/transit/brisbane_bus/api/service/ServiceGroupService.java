package com.wychesterso.transit.brisbane_bus.api.service;

import com.wychesterso.transit.brisbane_bus.api.dto.*;
import com.wychesterso.transit.brisbane_bus.api.service.cache.ServiceGroupAtStopList;
import com.wychesterso.transit.brisbane_bus.api.service.cache.ServiceGroupDTO;
import com.wychesterso.transit.brisbane_bus.api.service.cache.ServiceGroupList;
import com.wychesterso.transit.brisbane_bus.st.model.ServiceGroupAtStop;
import com.wychesterso.transit.brisbane_bus.st.model.ServiceGroupKey;
import com.wychesterso.transit.brisbane_bus.st.repository.ServiceGroupRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceGroupService {

    private final ServiceGroupRepository serviceGroupRepository;
    private final ServiceGroupStopLocator serviceGroupStopLocator;
    private final ArrivalsService arrivalsService;
    private final StopSequenceService stopSequenceService;

    private final RedisTemplate<String, Object> redis;

    public ServiceGroupService(
            ServiceGroupRepository serviceGroupRepository,
            ServiceGroupStopLocator serviceGroupStopLocator,
            ArrivalsService arrivalsService,
            StopSequenceService stopSequenceService,
            RedisTemplate<String, Object> redis) {
        this.serviceGroupRepository = serviceGroupRepository;
        this.serviceGroupStopLocator = serviceGroupStopLocator;
        this.arrivalsService = arrivalsService;
        this.stopSequenceService = stopSequenceService;
        this.redis = redis;
    }

    public List<BriefServiceResponse> getServicesByPrefix(String prefix, double lat, double lon) {
        if (prefix == null || prefix.isBlank()) return List.of();

        List<ServiceGroupDTO> cachedDTOs;
        String key = "services:prefix:%s".formatted(prefix);

        @SuppressWarnings("unchecked")
        ServiceGroupList cached = (ServiceGroupList) redis.opsForValue().get(key);
        if (cached != null) {
            System.out.println("Using cached Redis result: " + key);
            cachedDTOs = cached.serviceGroupList();
        } else {
            cachedDTOs = serviceGroupRepository.getServicesByPrefix(prefix)
                    .stream()
                    .map(sg -> new ServiceGroupDTO(
                            sg.getRouteShortName(),
                            sg.getRouteLongName(),
                            sg.getTripHeadsign(),
                            sg.getDirectionId(),
                            sg.getRouteColor(),
                            sg.getRouteTextColor()
                    ))
                    .toList();
            redis.opsForValue().set(
                    key,
                    new ServiceGroupList(cachedDTOs),
                    Duration.ofHours(24) // TTL
            );
        }

        return cachedDTOs.stream()
                .map(dto -> DTOtoResponse(dto, lat, lon))
                .toList();
    }

    public List<BriefServiceResponse> getServicesAtStop(String stopId, double lat, double lon) {

        List<ServiceGroupDTO> cachedDTOs;
        String key = "stop:%s:services".formatted(stopId);

        @SuppressWarnings("unchecked")
        ServiceGroupList cached = (ServiceGroupList) redis.opsForValue().get(key);
        if (cached != null) {
            System.out.println("Using cached Redis result: " + key);
            cachedDTOs = cached.serviceGroupList();
        } else {
            cachedDTOs = serviceGroupRepository.getServicesAtStop(stopId)
                    .stream()
                    .map(sg -> new ServiceGroupDTO(
                            sg.getRouteShortName(),
                            sg.getRouteLongName(),
                            sg.getTripHeadsign(),
                            sg.getDirectionId(),
                            sg.getRouteColor(),
                            sg.getRouteTextColor()
                    ))
                    .toList();
            redis.opsForValue().set(
                    key,
                    new ServiceGroupList(cachedDTOs),
                    Duration.ofHours(24) // TTL
            );
        }

        return cachedDTOs.stream()
                .map(dto -> DTOtoResponse(dto, lat, lon))
                .toList();
    }

    public List<BriefServiceResponse> getServicesAtStops(List<String> stopIds, double lat, double lon) {
        if (stopIds == null || stopIds.isEmpty()) return List.of();

        List<ServiceGroupAtStopDTO> rawResults = getCachedServiceGroupsAtStops(stopIds);

        Map<ServiceGroupKey, ServiceGroupAtStopDTO> closestPerRoute = new HashMap<>();

        for (ServiceGroupAtStopDTO sg : rawResults) {
            double distance = squaredDistance(lat, lon, sg);

            ServiceGroupKey keyRoute = new ServiceGroupKey(
                    sg.routeShortName(),
                    sg.tripHeadsign(),
                    sg.directionId()
            );

            closestPerRoute.merge(
                    keyRoute,
                    sg,
                    (a, b) -> squaredDistance(lat, lon, a) <= distance ? a : b
            );
        }

        // sort by distance and map to response
        return closestPerRoute.values().stream()
                .sorted(Comparator.comparingDouble(
                        sg -> squaredDistance(lat, lon, sg)
                ))
                .map(sg -> toResponse(sg, lat, lon))
                .toList();
    }

    public Map<String, List<BriefServiceResponse>> getServicesAtStopsPerStop(
            List<String> stopIds,
            double lat,
            double lon) {
        if (stopIds == null || stopIds.isEmpty()) return Map.of();

        List<ServiceGroupAtStopDTO> rawResults = getCachedServiceGroupsAtStops(stopIds);

        // group results by stopId
        Map<String, List<ServiceGroupAtStopDTO>> byStop = rawResults.stream()
                .collect(Collectors.groupingBy(ServiceGroupAtStopDTO::stopId));

        Map<String, List<BriefServiceResponse>> result = new HashMap<>();

        for (Map.Entry<String, List<ServiceGroupAtStopDTO>> entry : byStop.entrySet()) {
            String stopId = entry.getKey();

            List<BriefServiceResponse> responses = entry.getValue().stream()
                    .sorted(Comparator.comparingDouble(
                            sg -> squaredDistance(lat, lon, sg)
                    ))
                    .map(sg -> toResponseAtStop(sg, lat, lon))
                    .toList();

            result.put(stopId, responses);
        }

        return result;
    }

    private List<ServiceGroupAtStopDTO> getCachedServiceGroupsAtStops(List<String> stopIds) {
        List<String> sortedStopIds = new ArrayList<>(stopIds);
        Collections.sort(sortedStopIds);

        String raw = String.join(",", sortedStopIds);
        String hash = DigestUtils.sha256Hex(raw);
        String key = "stops:services:" + hash;

        @SuppressWarnings("unchecked")
        ServiceGroupAtStopList cached = (ServiceGroupAtStopList) redis.opsForValue().get(key);

        if (cached != null) return cached.serviceGroupAtStopList();

        List<ServiceGroupAtStopDTO> serviceGroups = serviceGroupRepository
                .getServicesAtStops(sortedStopIds.toArray(new String[0]))
                .stream()
                .map(ServiceGroupAtStopDTO::from)
                .toList();

        redis.opsForValue().set(
                key,
                new ServiceGroupAtStopList(serviceGroups),
                Duration.ofHours(24)
        );

        return serviceGroups;
    }

    private BriefServiceResponse toResponse(
            ServiceGroupAtStopDTO serviceGroup,
            double lat,
            double lon) {

        BriefStopResponse adjacentStop = serviceGroupStopLocator.getAdjacentStopForService(
                serviceGroup.routeShortName(),
                serviceGroup.tripHeadsign(),
                serviceGroup.directionId(),
                lat,
                lon
        );
        ArrivalsAtStopResponse arrivalsAtStopResponse = arrivalsService.getNextArrivalsForServiceAtStop(
                adjacentStop.stopId(),
                serviceGroup.routeShortName(),
                serviceGroup.tripHeadsign(),
                serviceGroup.directionId()
        );

        return new BriefServiceResponse(
                new ServiceId(
                        serviceGroup.routeShortName(),
                        serviceGroup.tripHeadsign(),
                        serviceGroup.directionId()
                ),
                serviceGroup.routeShortName(),
                serviceGroup.routeLongName(),
                arrivalsAtStopResponse
        );
    }

    private BriefServiceResponse toResponseAtStop(
            ServiceGroupAtStopDTO serviceGroup,
            double lat,
            double lon) {

        ArrivalsAtStopResponse arrivalsAtStopResponse = arrivalsService.getNextArrivalsForServiceAtStop(
                serviceGroup.stopId(),
                serviceGroup.routeShortName(),
                serviceGroup.tripHeadsign(),
                serviceGroup.directionId()
        );

        return new BriefServiceResponse(
                new ServiceId(
                        serviceGroup.routeShortName(),
                        serviceGroup.tripHeadsign(),
                        serviceGroup.directionId()
                ),
                serviceGroup.routeShortName(),
                serviceGroup.routeLongName(),
                arrivalsAtStopResponse
        );
    }

    private BriefServiceResponse DTOtoResponse(
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

    private double squaredDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {
        return Math.pow(lat1 - lat2, 2) + Math.pow(lon1 - lon2, 2);
    }

    private double squaredDistance(
            double lat,
            double lon,
            ServiceGroupAtStopDTO sg) {
        return squaredDistance(lat, lon, sg.stopLat(), sg.stopLon());
    }
}
