package com.wychesterso.transit.brisbane_bus.gtfs;

public class LoadHelper {
    public static Integer parseInteger(String s) {
        if (s == null || s.isBlank()) return null;
        return Integer.parseInt(s);
    }

    public static Double parseDouble(String s) {
        if (s == null || s.isBlank()) return null;
        return Double.parseDouble(s);
    }
}
