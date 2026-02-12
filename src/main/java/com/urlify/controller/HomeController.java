package com.urlify.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Home Controller for API information
 */
@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "URLify - URL Shortening Platform");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("endpoints", Map.of(
                "register", "POST /api/auth/register",
                "login", "POST /api/auth/login",
                "shorten", "POST /api/urls/shorten (requires auth)",
                "redirect", "GET /{shortCode}",
                "analytics", "GET /api/analytics/{shortCode} (requires auth)"));
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("database", "MongoDB");
        response.put("cache", "Redis");
        return response;
    }
}
