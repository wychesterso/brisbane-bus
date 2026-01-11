package com.wychesterso.transit.brisbane_bus.service;

import com.wychesterso.transit.brisbane_bus.dto.RouteAtStopArrivalDTO;
import com.wychesterso.transit.brisbane_bus.dto.RouteAtStopArrivalResponse;
import com.wychesterso.transit.brisbane_bus.dto.StopArrivalDTO;
import com.wychesterso.transit.brisbane_bus.repository.StopArrivalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StopArrivalService {

    private final StopArrivalRepository repository;

    private static final ZoneId BRISBANE = ZoneId.of("Australia/Brisbane");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public StopArrivalService(StopArrivalRepository repository) {
        this.repository = repository;
    }

    public List<StopArrivalDTO> getNextArrivalsForStop(String stopId) {
        int nowSeconds = LocalTime.now().toSecondOfDay();
        return repository.findNextArrivalsForStop(stopId, nowSeconds);
    }

    public List<RouteAtStopArrivalResponse> getNextArrivalsForRouteAtStop(String stopId, String routeId) {
        int nowSeconds = LocalTime.now(BRISBANE).toSecondOfDay();
        LocalDate serviceDate = LocalDate.now(BRISBANE);

        return repository.findNextArrivalsForRouteAtStop(stopId, routeId, nowSeconds)
                .stream()
                .map(r -> {
                    LocalDateTime arrival =
                            serviceDate.atStartOfDay().plusSeconds(r.getArrivalTimeSeconds());

                    LocalDateTime departure =
                            serviceDate.atStartOfDay().plusSeconds(r.getDepartureTimeSeconds());

                    return new RouteAtStopArrivalResponse(
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