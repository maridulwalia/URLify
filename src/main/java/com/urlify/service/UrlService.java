package com.urlify.service;

import com.urlify.dto.ShortenUrlRequest;
import com.urlify.dto.UrlResponse;
import com.urlify.entity.Url;
import com.urlify.entity.User;
import com.urlify.exception.ResourceNotFoundException;
import com.urlify.repository.UrlRepository;
import com.urlify.repository.UserRepository;
import com.urlify.util.Base62Encoder;
import com.urlify.util.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * URL Service for creating and managing short URLs
 */
@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Base62Encoder base62Encoder;

    @Autowired
    private UrlValidator urlValidator;

    @Value("${server.port:8080}")
    private String serverPort;

    private static final String BASE_URL = "http://localhost:";

    /**
     * Create a shortened URL
     */
    public UrlResponse shortenUrl(ShortenUrlRequest request, String userEmail) {
        // Validate URL
        String validationError = urlValidator.getValidationError(request.getUrl());
        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }

        // Get user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create URL entity
        Url url = new Url();
        url.setOriginalUrl(request.getUrl());
        url.setUserId(user.getId());

        // Handle custom alias or generate short code
        if (request.getCustomAlias() != null && !request.getCustomAlias().trim().isEmpty()) {
            String customAlias = request.getCustomAlias().trim();
            if (urlRepository.existsByShortCode(customAlias)) {
                throw new IllegalArgumentException("Custom alias already in use");
            }
            url.setShortCode(customAlias);
        } else {
            // Generate unique short code
            url.setShortCode(generateUniqueShortCode());
        }

        // Set expiry if provided
        if (request.getExpiryHours() != null && request.getExpiryHours() > 0) {
            url.setExpiresAt(LocalDateTime.now().plusHours(request.getExpiryHours()));
        }

        // Save URL
        Url savedUrl = urlRepository.save(url);

        return mapToResponse(savedUrl);
    }

    /**
     * Get all URLs for a user
     */
    public List<UrlResponse> getUserUrls(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Url> urls = urlRepository.findByUserId(user.getId());
        return urls.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all URLs for a user with pagination
     */
    public org.springframework.data.domain.Page<UrlResponse> getUserUrls(String userEmail,
            org.springframework.data.domain.Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        org.springframework.data.domain.Page<Url> urls = urlRepository.findByUserId(user.getId(), pageable);
        return urls.map(this::mapToResponse);
    }

    /**
     * Delete a URL by short code
     */
    public void deleteUrl(String shortCode, String userEmail) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("URL not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user owns this URL
        if (!url.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to delete this URL");
        }

        urlRepository.delete(url);
    }

    /**
     * Generate unique short code using Base62
     */
    private String generateUniqueShortCode() {
        String shortCode;
        int attempts = 0;
        do {
            // Use timestamp + random for uniqueness
            long timestamp = System.currentTimeMillis();
            long random = (long) (Math.random() * 1000);
            long combined = timestamp + random;

            shortCode = base62Encoder.encode(combined);

            // Take last 7 characters for shorter codes
            if (shortCode.length() > 7) {
                shortCode = shortCode.substring(shortCode.length() - 7);
            }

            attempts++;
            if (attempts > 10) {
                throw new RuntimeException("Failed to generate unique short code");
            }
        } while (urlRepository.existsByShortCode(shortCode));

        return shortCode;
    }

    /**
     * Map URL entity to response DTO
     */
    private UrlResponse mapToResponse(Url url) {
        return UrlResponse.builder()
                .id(url.getId())
                .originalUrl(url.getOriginalUrl())
                .shortCode(url.getShortCode())
                .shortUrl(BASE_URL + serverPort + "/" + url.getShortCode())
                .clicks(url.getClicks())
                .expiresAt(url.getExpiresAt())
                .createdAt(url.getCreatedAt())
                .build();
    }
}
