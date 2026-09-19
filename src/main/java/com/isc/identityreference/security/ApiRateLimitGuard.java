package com.isc.identityreference.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ApiRateLimitGuard {
    private final ApiRateLimitProperties properties;
    private final Clock clock;
    private final ConcurrentMap<String, Window> windows = new ConcurrentHashMap<>();

    @Autowired
    public ApiRateLimitGuard(ApiRateLimitProperties properties) {
        this(properties, Clock.systemUTC());
    }

    ApiRateLimitGuard(ApiRateLimitProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    public boolean allow(String principal) {
        if (!properties.isEnabled()) return true;
        String key = principal == null || principal.isBlank() ? "anonymous" : principal;
        long epochMinute = Instant.now(clock).getEpochSecond() / 60;
        Window next = windows.compute(key, (ignored, current) -> {
            if (current == null || current.epochMinute != epochMinute) {
                return new Window(epochMinute, 1);
            }
            return new Window(epochMinute, current.count + 1);
        });
        if (next.count <= properties.getRequestsPerMinute()) {
            return true;
        }
        windows.remove(key, next);
        return false;
    }

    private record Window(long epochMinute, int count) {}
}
