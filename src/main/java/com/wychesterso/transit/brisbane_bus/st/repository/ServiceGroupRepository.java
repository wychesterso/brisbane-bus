package com.wychesterso.transit.brisbane_bus.st.repository;

import com.wychesterso.transit.brisbane_bus.st.model.ServiceGroup;
import com.wychesterso.transit.brisbane_bus.st.model.ServiceGroupAtStop;
import com.wychesterso.transit.brisbane_bus.st.model.StopTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceGroupRepository extends JpaRepository<StopTime, String> {

    @Query(
            value = """
                    SELECT DISTINCT
                        r.route_short_name AS routeShortName,
                        r.route_long_name AS routeLongName,
                        t.trip_headsign AS tripHeadsign,
                        t.direction_id AS directionId,
                        r.route_color AS routeColor,
                        r.route_text_color AS routeTextColor
                    FROM routes r
                    JOIN trips t ON r.route_id = t.route_id
                    WHERE r.route_short_name ILIKE CONCAT(:prefix, '%')
                    ORDER BY r.route_short_name;
                    """,
            nativeQuery = true
    )
    List<ServiceGroup> getServicesByPrefix(
            @Param("prefix") String prefix
    );

    @Query(
            value = """
                    SELECT DISTINCT
                        r.route_short_name AS routeShortName,
                        r.route_long_name AS routeLongName,
                        t.trip_headsign AS tripHeadsign,
                        t.direction_id AS directionId,
                        r.route_color AS routeColor,
                        r.route_text_color AS routeTextColor
                    FROM routes r
                    JOIN trips t ON r.route_id = t.route_id
                    JOIN stop_times st ON t.trip_id = st.trip_id
                    WHERE st.stop_id = :stopId
                    ORDER BY r.route_short_name;
                    """,
            nativeQuery = true
    )
    List<ServiceGroup> getServicesAtStop(
            @Param("stopId") String stopId
    );

    @Query(
            value = """
                    SELECT
                        routeShortName,
                        routeLongName,
                        tripHeadsign,
                        directionId,
                        routeColor,
                        routeTextColor
                    FROM (
                        SELECT
                            r.route_short_name AS routeShortName,
                            r.route_long_name AS routeLongName,
                            t.trip_headsign AS tripHeadsign,
                            t.direction_id AS directionId,
                            r.route_color AS routeColor,
                            r.route_text_color AS routeTextColor,
                            st.stop_id AS stopId,
                            POWER(s.stop_lat - :lat, 2) + POWER(s.stop_lon - :lon, 2) AS distance,
                            ROW_NUMBER() OVER (
                                PARTITION BY r.route_short_name
                                ORDER BY
                                    POWER(s.stop_lat - :lat, 2)
                                  + POWER(s.stop_lon - :lon, 2)
                            ) AS rn
                        FROM stop_times st
                        JOIN trips t ON st.trip_id = t.trip_id
                        JOIN routes r ON t.route_id = r.route_id
                        JOIN stops s ON st.stop_id = s.stop_id
                        WHERE st.stop_id = ANY(:stopIds)
                    ) x
                    WHERE rn = 1
                    ORDER BY distance, routeShortName;
                    """,
            nativeQuery = true
    )
    List<ServiceGroup> getServicesAtStops(
            @Param("stopIds") String[] stopIds,
            @Param("lat") Double lat,
            @Param("lon") Double lon
    );

    @Query(
            value = """
                    SELECT
                        r.route_short_name AS routeShortName,
                        r.route_long_name AS routeLongName,
                        t.trip_headsign AS tripHeadsign,
                        t.direction_id AS directionId,
                        r.route_color AS routeColor,
                        r.route_text_color AS routeTextColor,
                        st.stop_id AS stopId,
                        s.stop_lat AS stopLat,
                        s.stop_lon AS stopLon
                    FROM stop_times st
                    JOIN trips t ON st.trip_id = t.trip_id
                    JOIN routes r ON t.route_id = r.route_id
                    JOIN stops s ON st.stop_id = s.stop_id
                    WHERE st.stop_id = ANY(:stopIds)
                    """,
            nativeQuery = true
    )
    List<ServiceGroupAtStop> getServicesAtStops(
            @Param("stopIds") String[] stopIds
    );
}
