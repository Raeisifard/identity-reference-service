package com.isc.identityreference.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Order(100)
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LogManager.getLogger(RequestLoggingFilter.class);
    private final ObservabilityProperties properties;

    public RequestLoggingFilter(ObservabilityProperties properties) { this.properties = properties; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        long started = System.nanoTime();
        try { chain.doFilter(request, response); }
        finally {
            ifEnabled(request, response, started);
        }
    }

    private void ifEnabled(HttpServletRequest request, HttpServletResponse response, long started) {
        if (!properties.isRequestLoggingEnabled()) return;
        log.info("http_request method={} path={} status={} durationMs={}",
                request.getMethod(), request.getRequestURI(), response.getStatus(),
                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
    }
}
