package com.wychesterso.transit.brisbane_bus.gtfs;

import com.opencsv.exceptions.CsvValidationException;
import com.wychesterso.transit.brisbane_bus.model.Stop;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StopLoader {
    public static List<Stop> loadStops() throws IOException, CsvValidationException {
        List<Stop> stops = new ArrayList<>();

        try (CSVReader reader = new CSVReader(
                new FileReader("src/main/resources/static/SEQ_GTFS/stops.txt"))) {

            reader.readNext();
            String[] row;

            while ((row = reader.readNext()) != null) {
                stops.add(new Stop(
                        row[0],
                        row[1],
                        row[2],
                        row[3],
                        LoadHelper.parseDouble(row[4]),
                        LoadHelper.parseDouble(row[5]),
                        row[6],
                        row[7],
                        row[8].isEmpty() ? null : Integer.valueOf(row[8]),
                        row[9],
                        row[10]
                ));
            }
        }

        return stops;
    }
}
