package com.wychesterso.transit.brisbane_bus.gtfs;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.wychesterso.transit.brisbane_bus.model.Trip;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TripLoader {
    public static List<Trip> loadTrips() throws IOException, CsvValidationException {
        List<Trip> trips = new ArrayList<>();

        try (CSVReader reader = new CSVReader(
                new FileReader("src/main/resources/static/SEQ_GTFS/trips.txt"))) {

            reader.readNext();
            String[] row;

            while ((row = reader.readNext()) != null) {
                trips.add(new Trip(
                        row[2],
                        row[0],
                        row[1],
                        row[3],
                        LoadHelper.parseInteger(row[4]),
                        row[5],
                        row[6]
                ));
            }
        }

        return trips;
    }
}
