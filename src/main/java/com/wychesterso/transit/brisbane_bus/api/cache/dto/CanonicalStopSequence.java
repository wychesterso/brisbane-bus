package com.wychesterso.transit.brisbane_bus.api.cache.dto;

import java.util.Map;

public record CanonicalStopSequence(
        Map<String, Integer> stopIdToSequenceMap
) {}
