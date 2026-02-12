package com.urlify.controller;

import com.urlify.dto.AnalyticsResponse;
import com.urlify.service.AnalyticsService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Analytics Controller for retrieving URL statistics
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * Get analytics for a specific short code
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<AnalyticsResponse> getAnalytics(@PathVariable String shortCode,
            Authentication authentication) {
        String userEmail = authentication.getName();
        AnalyticsResponse analytics = analyticsService.getAnalytics(shortCode, userEmail);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get all analytics for the authenticated user
     */
    @GetMapping("/all")
    public ResponseEntity<List<AnalyticsResponse>> getAllAnalytics(Authentication authentication) {
        String userEmail = authentication.getName();
        List<AnalyticsResponse> analytics = analyticsService.getAllAnalytics(userEmail);
        return ResponseEntity.ok(analytics);
    }
}
