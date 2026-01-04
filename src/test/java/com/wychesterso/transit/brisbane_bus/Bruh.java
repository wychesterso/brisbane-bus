package com.wychesterso.transit.brisbane_bus;

import com.wychesterso.transit.brisbane_bus.gtfs.RouteLoader;
import com.wychesterso.transit.brisbane_bus.gtfs.StopLoader;
import com.wychesterso.transit.brisbane_bus.gtfs.StopTimeLoader;
import com.wychesterso.transit.brisbane_bus.gtfs.TripLoader;
import com.wychesterso.transit.brisbane_bus.model.Route;
import com.wychesterso.transit.brisbane_bus.model.Stop;
import com.wychesterso.transit.brisbane_bus.model.StopTime;
import com.wychesterso.transit.brisbane_bus.model.Trip;

public class Bruh {
    public static void main(String[] args) {
        printStopTimes();
    }

    private static void printStops() {
        try {
            for (Stop stop : StopLoader.loadStops()) {
                System.out.println(stop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printRoutes() {
        try {
            for (Route route : RouteLoader.loadRoutes()) {
                System.out.println(route);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printTrips() {
        try {
            for (Trip trip : TripLoader.loadTrips()) {
                System.out.println(trip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printStopTimes() {
        try {
            for (StopTime stopTime : StopTimeLoader.loadStopTimes()) {
                System.out.println(stopTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
