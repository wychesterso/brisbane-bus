package com.wychesterso.transit.brisbane_bus.bootstrap;

import com.wychesterso.transit.brisbane_bus.gtfs.RouteLoader;
import com.wychesterso.transit.brisbane_bus.gtfs.StopLoader;
import com.wychesterso.transit.brisbane_bus.gtfs.StopTimeLoader;
import com.wychesterso.transit.brisbane_bus.gtfs.TripLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GtfsBootstrap implements CommandLineRunner {

    private final RouteLoader routeLoader;
    private final StopLoader stopLoader;
    private final TripLoader tripLoader;
    private final StopTimeLoader stopTimeLoader;

    @Value("${gtfs.load-on-startup:true}")
    private boolean loadOnStartup;

    public GtfsBootstrap(
            RouteLoader routeLoader,
            StopLoader stopLoader,
            TripLoader tripLoader,
            StopTimeLoader stopTimeLoader
    ) {
        this.routeLoader = routeLoader;
        this.stopLoader = stopLoader;
        this.tripLoader = tripLoader;
        this.stopTimeLoader = stopTimeLoader;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!loadOnStartup) return;
        routeLoader.loadRoutes();
        stopLoader.loadStops();
        tripLoader.loadTrips();
        stopTimeLoader.loadStopTimes();
    }
}