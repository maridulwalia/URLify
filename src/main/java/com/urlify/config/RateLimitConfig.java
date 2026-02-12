package com.urlify.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiting Configuration using Bucket4j
 */
@Configuration
public class RateLimitConfig {

    @Value("${rate.limit.public.capacity}")
    private long publicCapacity;

    @Value("${rate.limit.public.refill-tokens}")
    private long publicRefillTokens;

    @Value("${rate.limit.public.refill-duration}")
    private long publicRefillDuration;

    @Value("${rate.limit.authenticated.capacity}")
    private long authenticatedCapacity;

    @Value("${rate.limit.authenticated.refill-tokens}")
    private long authenticatedRefillTokens;

    @Value("${rate.limit.authenticated.refill-duration}")
    private long authenticatedRefillDuration;

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    /**
     * Get or create bucket for public (IP-based) rate limiting
     */
    public Bucket resolvePublicBucket(String key) {
        return cache.computeIfAbsent(key, k -> createPublicBucket());
    }

    /**
     * Get or create bucket for authenticated user rate limiting
     */
    public Bucket resolveAuthenticatedBucket(String key) {
        return cache.computeIfAbsent(key, k -> createAuthenticatedBucket());
    }

    private Bucket createPublicBucket() {
        Bandwidth limit = Bandwidth.classic(
                publicCapacity,
                Refill.intervally(publicRefillTokens, Duration.ofSeconds(publicRefillDuration)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket createAuthenticatedBucket() {
        Bandwidth limit = Bandwidth.classic(
                authenticatedCapacity,
                Refill.intervally(authenticatedRefillTokens, Duration.ofSeconds(authenticatedRefillDuration)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
