package com.wychesterso.transit.brisbane_bus.api.service.time;

import java.time.LocalDate;

public record ServiceClock(
        LocalDate serviceDate,
        int serviceSeconds
) {}