package com.wychesterso.transit.brisbane_bus.service.time;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ServiceTimeHelper {

    private static final ZoneId BRISBANE = ZoneId.of("Australia/Brisbane");
    private static final LocalTime DAY_CUTOFF = LocalTime.of(3, 0); // 3am

    public static ServiceClock now() {
        ZonedDateTime now = ZonedDateTime.now(BRISBANE);

        LocalDate serviceDate = now.toLocalDate();
        int serviceSeconds = now.toLocalTime().toSecondOfDay();

        if (now.toLocalTime().isBefore(DAY_CUTOFF)) {
            serviceDate = serviceDate.minusDays(1);
            serviceSeconds += 24 * 3600;
        }

        return new ServiceClock(serviceDate, serviceSeconds);
    }
}