package com.urlify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * URLify Application - Scalable URL Shortening and Analytics Platform
 */
@SpringBootApplication
@EnableMongoAuditing
@EnableAsync
public class UrlifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlifyApplication.class, args);
        System.out.println("\n" +
                "╔══════════════════════════════════════════════════════════╗\n" +
                "║                                                          ║\n" +
                "║              URLify - URL Shortening Platform            ║\n" +
                "║                                                          ║\n" +
                "║  🚀 Server is running on http://localhost:8080          ║\n" +
                "║                                                          ║\n" +
                "║  📚 API Endpoints:                                       ║\n" +
                "║     POST /api/auth/register - Register user             ║\n" +
                "║     POST /api/auth/login - Login user                   ║\n" +
                "║     POST /api/urls/shorten - Create short URL           ║\n" +
                "║     GET  /api/urls/my-urls - Get user's URLs            ║\n" +
                "║     GET  /{shortCode} - Redirect to original URL        ║\n" +
                "║     GET  /api/analytics/{shortCode} - Get analytics     ║\n" +
                "║                                                          ║\n" +
                "╚══════════════════════════════════════════════════════════╝\n");
    }
}
