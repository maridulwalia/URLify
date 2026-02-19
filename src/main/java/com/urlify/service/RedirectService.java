package com.urlify.service;

import com.urlify.entity.Url;
import com.urlify.exception.ResourceNotFoundException;
import com.urlify.repository.UrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redirect Service with cache-first strategy
 */
@Service
public class RedirectService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AnalyticsService analyticsService;

    private static final String CACHE_PREFIX = "url:";
    private static final long CACHE_TTL = 1; // 1 hour

    /**
     * Get original URL from short code with cache-first strategy
     */
    public String getOriginalUrl(String shortCode, HttpServletRequest request) {
        // Extract request data now (before async handoff — request won't be available
        // later)
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        // Try cache first (O(1))
        String cacheKey = CACHE_PREFIX + shortCode;
        String originalUrl = null;

        try {
            originalUrl = (String) redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            System.err.println("Redis error (falling back to DB): " + e.getMessage());
        }

        if (originalUrl != null) {
            // Cache hit - track analytics asynchronously and return immediately
            analyticsService.trackClick(shortCode, ipAddress, userAgent, referer);
            return originalUrl;
        }

        // Cache miss - query database (O(log n) with index)
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));

        // Check if expired
        if (url.isExpired()) {
            throw new ResourceNotFoundException("Short URL has expired");
        }

        // Warm cache for future requests
        try {
            redisTemplate.opsForValue().set(cacheKey, url.getOriginalUrl(), CACHE_TTL, TimeUnit.HOURS);
        } catch (Exception e) {
            System.err.println("Redis error (cache not updated): " + e.getMessage());
        }

        // Track analytics asynchronously
        analyticsService.trackClick(shortCode, ipAddress, userAgent, referer);

        return url.getOriginalUrl();
    }

    /**
     * Extract client IP address from request
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
