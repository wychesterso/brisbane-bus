package com.wychesterso.transit.brisbane_bus.rt;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Responsible for scheduling when to poll GTFS-RT, updating the snapshot and handling failures
 */
@Component
@EnableScheduling
public class GtfsRtScheduler {

    private final GtfsRtFetcher client;
    private final GtfsRtSnapshot snapshot;

    public GtfsRtScheduler(
            GtfsRtFetcher client,
            GtfsRtSnapshot snapshot) {
        this.client = client;
        this.snapshot = snapshot;
    }

    @Scheduled(fixedDelayString = "${gtfs.rt.poll-ms:15000}")
    public void pollTripUpdates() {
        try {
            FeedMessage feed = client.fetchTripUpdates();
            logStuff(feed);
            snapshot.update(feed);
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass())
                    .warn("Failed to poll GTFS-RT TripUpdates", e);
        }
    }

    private void logStuff(FeedMessage feed) {
        feed.getEntityList().stream()
                .filter(FeedEntity::hasTripUpdate)
                .limit(3)
                .forEach(entity -> {

                    GtfsRealtime.TripUpdate tu = entity.getTripUpdate();

                    LoggerFactory.getLogger(getClass()).info(
                            "TripUpdate: tripId={}, routeId={}, stopUpdates={}",
                            tu.getTrip().getTripId(),
                            tu.getTrip().getRouteId(),
                            tu.getStopTimeUpdateCount()
                    );
                });
    }
}