package com.wychesterso.transit.brisbane_bus.gtfs;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.wychesterso.transit.brisbane_bus.model.StopTime;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StopTimeLoader {
    public static List<StopTime> loadStopTimes() throws IOException, CsvValidationException {
        List<StopTime> stops = new ArrayList<>();

        try (CSVReader reader = new CSVReader(
                new FileReader("src/main/resources/static/SEQ_GTFS/stop_times.txt"))) {

            reader.readNext();
            String[] row;

            while ((row = reader.readNext()) != null) {
                stops.add(new StopTime(
                        row[0],
                        parseGtfsTime(row[1]),
                        parseGtfsTime(row[2]),
                        row[3],
                        LoadHelper.parseInteger(row[4]),
                        LoadHelper.parseInteger(row[5]),
                        LoadHelper.parseInteger(row[6])
                ));
            }
        }

        return stops;
    }

    private static Integer parseGtfsTime(String t) {
        String[] p = t.split(":");
        return Integer.parseInt(p[0]) * 3600
                + Integer.parseInt(p[1]) * 60
                + Integer.parseInt(p[2]);
    }
}
