package com.wychesterso.transit.brisbane_bus.gtfs;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.wychesterso.transit.brisbane_bus.model.Stop;
import com.wychesterso.transit.brisbane_bus.repository.StopRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;

@Component
public class StopLoader {

    private final StopRepository stopRepository;

    public StopLoader(StopRepository stopRepository) {
        this.stopRepository = stopRepository;
    }

    @Transactional
    public void loadStops() throws IOException, CsvValidationException {

        try (CSVReader reader = new CSVReader(
                new FileReader("src/main/resources/static/SEQ_GTFS/stops.txt"))) {

            reader.readNext(); // header
            String[] row;

            while ((row = reader.readNext()) != null) {
                stopRepository.save(
                        new Stop(
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
                        )
                );
            }
        }
    }
}
