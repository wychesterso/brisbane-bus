package com.wychesterso.transit.brisbane_bus.api.controller;

import com.wychesterso.transit.brisbane_bus.api.dto.BriefStopResponse;
import com.wychesterso.transit.brisbane_bus.api.dto.FullStopResponse;
import com.wychesterso.transit.brisbane_bus.api.service.AdjacentService;
import com.wychesterso.transit.brisbane_bus.api.service.StopService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stops")
public class StopController {

    private final StopService stopService;
    private final AdjacentService adjacentService;

    public StopController(StopService stopService, AdjacentService adjacentService) {
        this.stopService = stopService;
        this.adjacentService = adjacentService;
    }

    @GetMapping("/nearest")
    public List<BriefStopResponse> getNearestStops(
            @RequestParam(required = true) Double lat,
            @RequestParam(required = true) Double lon) {
        return adjacentService.getAdjacentStopsBrief(lat, lon);
    }
}
