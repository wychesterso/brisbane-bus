package com.wychesterso.transit.brisbane_bus.api.controller;

import com.wychesterso.transit.brisbane_bus.api.dto.BriefStopResponse;
import com.wychesterso.transit.brisbane_bus.api.service.StopService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stops")
public class StopController {

    private final StopService stopService;

    public StopController(StopService stopService) {
        this.stopService = stopService;
    }

    @GetMapping("/{stopId}")
    public BriefStopResponse getStop(
            @PathVariable String stopId) {
        return stopService.getStop(stopId);
    }
}
