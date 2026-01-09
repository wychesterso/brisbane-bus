package com.wychesterso.transit.brisbane_bus.gtfs;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.wychesterso.transit.brisbane_bus.model.StopTime;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;

@Component
public class StopTimeLoader {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void loadStopTimes() throws IOException, CsvValidationException {

        int limit = 20000; // TEMP

        try (CSVReader reader = new CSVReader(
                new FileReader("src/main/resources/static/SEQ_GTFS/stop_times.txt"))) {

            reader.readNext(); // header
            String[] row;
            int count = 0;

            while ((row = reader.readNext()) != null && limit-- >= 0) {
                StopTime st = new StopTime(
                        row[0],
                        LoadHelper.parseGtfsTime(row[1]),
                        LoadHelper.parseGtfsTime(row[2]),
                        row[3],
                        LoadHelper.parseInteger(row[4]),
                        LoadHelper.parseInteger(row[5]),
                        LoadHelper.parseInteger(row[6])
                );

                em.persist(st);

                // flush in batches
                if (++count % 500 == 0) {
                    em.flush();
                    em.clear();
                }
            }
        }
    }
}