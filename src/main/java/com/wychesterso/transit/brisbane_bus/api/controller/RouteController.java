package com.wychesterso.transit.brisbane_bus.api.controller;

import com.wychesterso.transit.brisbane_bus.api.service.RouteService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }
}
