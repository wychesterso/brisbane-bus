package com.wychesterso.transit.brisbane_bus.api.cache;

import com.wychesterso.transit.brisbane_bus.api.cache.dto.ServiceGroupAtStopList;
import com.wychesterso.transit.brisbane_bus.api.cache.dto.ServiceGroupDTO;
import com.wychesterso.transit.brisbane_bus.api.cache.dto.ServiceGroupList;
import com.wychesterso.transit.brisbane_bus.api.dto.ServiceGroupAtStopDTO;
import com.wychesterso.transit.brisbane_bus.st.repository.ServiceGroupRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class ServiceGroupCache {

    private static final Duration TTL = Duration.ofHours(24);

    private final ServiceGroupRepository repository;
    private final RedisTemplate<String, Object> redis;

    public ServiceGroupCache(
            ServiceGroupRepository repository,
            RedisTemplate<String, Object> redis
    ) {
        this.repository = repository;
        this.redis = redis;
    }

    /**
     * Get a list of services, filtered by prefix
     * @param prefix the prefix search term
     * @return list of service data objects
     */
    public List<ServiceGroupDTO> getServicesByPrefix(String prefix) {
        String key = keyByPrefix(prefix);

        @SuppressWarnings("unchecked")
        ServiceGroupList cached = (ServiceGroupList) redis.opsForValue().get(key);
        if (cached != null) return cached.serviceGroupList();

        List<ServiceGroupDTO> result = repository.getServicesByPrefix(prefix)
                .stream()
                .map(ServiceGroupDTO::from)
                .toList();

        redis.opsForValue().set(
                key,
                new ServiceGroupList(result),
                TTL);

        return result;
    }

    private String keyByPrefix(String prefix) {
        return "services:prefix:" + prefix.toLowerCase();
    }

    /**
     * Get a list of services at a particular stop
     * @param stopId the stop to query
     * @return list of service-at-stop data objects
     */
    public List<ServiceGroupAtStopDTO> getServicesAtStop(String stopId) {
        String key = keyAtStop(stopId);

        @SuppressWarnings("unchecked")
        ServiceGroupAtStopList cached = (ServiceGroupAtStopList) redis.opsForValue().get(key);

        if (cached != null) return cached.serviceGroupAtStopList();

        List<ServiceGroupAtStopDTO> result = repository.getServicesAtStop(stopId)
                .stream()
                .map(ServiceGroupAtStopDTO::from)
                .toList();

        redis.opsForValue().set(
                key,
                new ServiceGroupAtStopList(result),
                TTL);

        return result;
    }

    private String keyAtStop(String stopId) {
        return "stop:" + stopId + ":services";
    }
}
