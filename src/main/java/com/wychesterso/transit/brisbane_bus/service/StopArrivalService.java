package com.wychesterso.transit.brisbane_bus.service;

import com.wychesterso.transit.brisbane_bus.dto.StopArrivalDTO;
import com.wychesterso.transit.brisbane_bus.dto.StopArrivalResponse;
import com.wychesterso.transit.brisbane_bus.repository.StopArrivalRepository;
import com.wychesterso.transit.brisbane_bus.service.time.ServiceClock;
import com.wychesterso.transit.brisbane_bus.service.time.ServiceTimeHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StopArrivalService {

    private final StopArrivalRepository repository;
    private final RedisTemplate<String, List<StopArrivalResponse>> redis;

    private static final ZoneId BRISBANE = ZoneId.of("Australia/Brisbane");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public StopArrivalService(
            StopArrivalRepository repository,
            RedisTemplate<String, List<StopArrivalResponse>> redis) {
        this.repository = repository;
        this.redis = redis;
    }

    public List<StopArrivalResponse> getNextArrivalsForStop(String stopId) {
        ServiceClock clock = ServiceTimeHelper.now();
        int nowSeconds = clock.serviceSeconds();
        LocalDate serviceDate = clock.serviceDate();

        return mapDTOtoResponse(
                repository.findNextArrivalsForStop(stopId, nowSeconds),
                serviceDate
        );
    }

    public List<StopArrivalResponse> getNextArrivalsForRouteAtStop(String stopId, String routeId) {
        ServiceClock clock = ServiceTimeHelper.now();
        int nowSeconds = clock.serviceSeconds();
        LocalDate serviceDate = clock.serviceDate();

        String key = "stop:%s:route:%s:%s"
                .formatted(stopId, routeId, serviceDate);

        @SuppressWarnings("unchecked")
        List<StopArrivalResponse> cached =
                (List<StopArrivalResponse>) redis.opsForValue().get(key);
        if (cached != null) return cached;

        List<StopArrivalResponse> result = mapDTOtoResponse(
                repository.findNextArrivalsForRouteAtStop(stopId, routeId, serviceDate, nowSeconds),
                serviceDate
        );

        redis.opsForValue().set(
                key,
                result,
                Duration.ofSeconds(30) // TTL
        );

        return result;
    }

    private List<StopArrivalResponse> mapDTOtoResponse(List<StopArrivalDTO> arrivals, LocalDate serviceDate) {

        return arrivals.stream()
                .map(r -> {
                    LocalDateTime arrival =
                            serviceDate.atStartOfDay().plusSeconds(r.getArrivalTimeSeconds());

                    LocalDateTime departure =
                            serviceDate.atStartOfDay().plusSeconds(r.getDepartureTimeSeconds());

                    return new StopArrivalResponse(
                            r.getTripId(),
                            r.getArrivalTimeSeconds(),
                            arrival.format(TIME_FMT),
                            r.getDepartureTimeSeconds(),
                            departure.format(TIME_FMT)
                    );
                })
                .toList();
    }
}