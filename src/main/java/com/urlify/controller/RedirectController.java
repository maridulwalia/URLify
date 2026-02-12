package com.urlify.controller;

import com.urlify.service.AnalyticsService;
import com.urlify.service.RedirectService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Redirect Controller for handling short URL redirects
 */
@RestController
public class RedirectController {

    @Autowired
    private RedirectService redirectService;

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * Redirect to original URL and track analytics
     */
    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        // Get original URL from cache or database
        String originalUrl = redirectService.getOriginalUrl(shortCode, request);

        // Track click asynchronously (non-blocking)
        try {
            analyticsService.trackClick(shortCode, request);
        } catch (Exception e) {
            // Log error but don't fail the redirect
            System.err.println("Failed to track analytics: " + e.getMessage());
        }

        // Redirect to original URL
        response.sendRedirect(originalUrl);
    }
}
