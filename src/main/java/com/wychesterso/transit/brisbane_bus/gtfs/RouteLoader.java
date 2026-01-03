package com.wychesterso.transit.brisbane_bus.gtfs;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.wychesterso.transit.brisbane_bus.model.Route;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RouteLoader {
    public static List<Route> loadRoutes() throws IOException, CsvValidationException {
        List<Route> routes = new ArrayList<>();

        try (CSVReader reader = new CSVReader(
                new FileReader("src/main/resources/static/SEQ_GTFS/routes.txt"))) {

            reader.readNext();
            String[] row;

            while ((row = reader.readNext()) != null) {
                routes.add(new Route(
                        row[0],
                        row[1],
                        row[2],
                        row[3],
                        LoadHelper.parseInteger(row[4]),
                        row[5],
                        row[6],
                        row[7]
                ));
            }
        }

        return routes;
    }
}
