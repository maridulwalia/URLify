package com.urlify.service;

import com.urlify.dto.AnalyticsResponse;
import com.urlify.entity.Analytics;
import com.urlify.entity.Url;
import com.urlify.exception.ResourceNotFoundException;
import com.urlify.repository.AnalyticsRepository;
import com.urlify.repository.UrlRepository;
import com.urlify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Analytics Service for tracking and retrieving click data
 */
@Service
public class AnalyticsService {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Track a click event asynchronously.
     * Called after redirect response is already sent to minimize latency.
     * Accepts pre-extracted request data since HttpServletRequest is not available
     * after the request completes.
     */
    @Async
    public void trackClick(String shortCode, String ipAddress, String userAgent, String referer) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("URL not found"));

        // Increment click count
        url.incrementClicks();
        urlRepository.save(url);

        // Create analytics record
        Analytics analytics = new Analytics();
        analytics.setShortCode(shortCode);
        analytics.setIpAddress(ipAddress);
        analytics.setUserAgent(userAgent);
        analytics.setReferer(referer);

        analyticsRepository.save(analytics);
    }

    /**
     * Get all analytics for a user
     */
    public List<AnalyticsResponse> getAllAnalytics(String userEmail) {
        String userId = getUserIdByEmail(userEmail);
        List<Url> userUrls = urlRepository.findByUserId(userId);

        return userUrls.stream()
                .map(url -> {
                    List<Analytics> analyticsRecords = analyticsRepository
                            .findByShortCodeOrderByTimestampDesc(url.getShortCode());

                    List<AnalyticsResponse.ClickDetail> clickDetails = analyticsRecords.stream()
                            .limit(10) // Limit to last 10 clicks for overview
                            .map(a -> AnalyticsResponse.ClickDetail.builder()
                                    .timestamp(a.getTimestamp())
                                    .ipAddress(a.getIpAddress())
                                    .userAgent(a.getUserAgent())
                                    .referer(a.getReferer())
                                    .build())
                            .collect(Collectors.toList());

                    return AnalyticsResponse.builder()
                            .shortCode(url.getShortCode())
                            .originalUrl(url.getOriginalUrl())
                            .totalClicks(url.getClicks())
                            .createdAt(url.getCreatedAt())
                            .expiresAt(url.getExpiresAt())
                            .recentClicks(clickDetails)
                            .build();
                })
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * Get analytics for a short code
     */
    public AnalyticsResponse getAnalytics(String shortCode, String userEmail) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("URL not found"));

        // Check if user owns this URL
        if (!url.getUserId().equals(getUserIdByEmail(userEmail))) {
            throw new IllegalArgumentException("You don't have permission to view analytics for this URL");
        }

        // Get analytics records
        List<Analytics> analyticsRecords = analyticsRepository.findByShortCodeOrderByTimestampDesc(shortCode);

        // Map to response
        List<AnalyticsResponse.ClickDetail> clickDetails = analyticsRecords.stream()
                .limit(100) // Limit to last 100 clicks
                .map(a -> AnalyticsResponse.ClickDetail.builder()
                        .timestamp(a.getTimestamp())
                        .ipAddress(a.getIpAddress())
                        .userAgent(a.getUserAgent())
                        .referer(a.getReferer())
                        .build())
                .collect(Collectors.toList());

        return AnalyticsResponse.builder()
                .shortCode(url.getShortCode())
                .originalUrl(url.getOriginalUrl())
                .totalClicks(url.getClicks())
                .createdAt(url.getCreatedAt())
                .expiresAt(url.getExpiresAt())
                .recentClicks(clickDetails)
                .build();
    }

    /**
     * Helper method to get user ID by email
     */
    private String getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
