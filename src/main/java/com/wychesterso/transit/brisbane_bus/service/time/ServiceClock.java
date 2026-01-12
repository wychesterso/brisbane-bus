package com.wychesterso.transit.brisbane_bus.service.time;

import java.time.LocalDate;

public record ServiceClock(
        LocalDate serviceDate,
        int serviceSeconds
) {}