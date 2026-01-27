package com.wychesterso.transit.brisbane_bus.api.controller;

import com.wychesterso.transit.brisbane_bus.api.dto.BriefServiceResponse;
import com.wychesterso.transit.brisbane_bus.api.dto.FullServiceResponse;
import com.wychesterso.transit.brisbane_bus.api.service.AdjacentService;
import com.wychesterso.transit.brisbane_bus.api.service.ServiceGroupService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceController {

    private final ServiceGroupService serviceGroupService;
    private final AdjacentService adjacentService;

    public ServiceController(
            ServiceGroupService serviceGroupService,
            AdjacentService adjacentService) {
        this.serviceGroupService = serviceGroupService;
        this.adjacentService = adjacentService;
    }

    @GetMapping("")
    public List<BriefServiceResponse> getServicesByPrefix(
            @RequestParam(required = true) String prefix,
            @RequestParam(required = true) Double lat,
            @RequestParam(required = true) Double lon) {
        return serviceGroupService.getServicesByPrefix(prefix, lat, lon);
    }

    @GetMapping("/nearest")
    public List<BriefServiceResponse> getNearestServices(
            @RequestParam(required = true) Double lat,
            @RequestParam(required = true) Double lon) {
        return adjacentService.getAdjacentServices(lat, lon);
    }

    @GetMapping("/{routeShortName}/{directionId}")
    public FullServiceResponse getFullServiceInfo(
            @RequestParam(required = true) String tripHeadsign) {
        // TODO
        return null;
    }
}
