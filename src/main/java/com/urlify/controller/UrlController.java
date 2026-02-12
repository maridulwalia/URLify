package com.urlify.controller;

import com.urlify.config.RateLimitConfig;
import com.urlify.dto.ShortenUrlRequest;
import com.urlify.dto.UrlResponse;
import com.urlify.service.UrlService;
import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * URL Controller for creating and managing short URLs
 */
@RestController
@RequestMapping("/api/urls")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @Autowired
    private RateLimitConfig rateLimitConfig;

    /**
     * Create a short URL (authenticated)
     */
    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@Valid @RequestBody ShortenUrlRequest request,
            Authentication authentication) {
        // Rate limiting for authenticated users
        String userEmail = authentication.getName();
        Bucket bucket = rateLimitConfig.resolveAuthenticatedBucket(userEmail);

        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Rate limit exceeded. Please try again later.");
        }

        UrlResponse response = urlService.shortenUrl(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all URLs for authenticated user with pagination
     */
    @GetMapping("/my-urls")
    public ResponseEntity<Page<UrlResponse>> getMyUrls(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userEmail = authentication.getName();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UrlResponse> urls = urlService.getUserUrls(userEmail, pageable);
        return ResponseEntity.ok(urls);
    }

    /**
     * Delete a URL by short code
     */
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<?> deleteUrl(@PathVariable String shortCode,
            Authentication authentication) {
        String userEmail = authentication.getName();
        urlService.deleteUrl(shortCode, userEmail);
        return ResponseEntity.ok("URL deleted successfully");
    }
}
