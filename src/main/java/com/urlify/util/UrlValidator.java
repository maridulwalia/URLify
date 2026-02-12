package com.urlify.util;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * URL validator to prevent malicious URLs and open redirect attacks
 */
@Component
public class UrlValidator {

    private static final int MAX_URL_LENGTH = 2048;
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?://)([\\w.-]+)(:[0-9]{1,5})?(/.*)?$",
            Pattern.CASE_INSENSITIVE);

    // Blocked domains and patterns
    private static final List<String> BLOCKED_DOMAINS = Arrays.asList(
            "localhost",
            "127.0.0.1",
            "0.0.0.0",
            "::1");

    private static final List<String> PRIVATE_IP_PATTERNS = Arrays.asList(
            "^10\\..*",
            "^172\\.(1[6-9]|2[0-9]|3[0-1])\\..*",
            "^192\\.168\\..*");

    /**
     * Validate URL for security and format
     * 
     * @param url The URL to validate
     * @return true if valid, false otherwise
     */
    public boolean isValid(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        // Check length
        if (url.length() > MAX_URL_LENGTH) {
            return false;
        }

        // Check format
        if (!URL_PATTERN.matcher(url).matches()) {
            return false;
        }

        try {
            URL parsedUrl = new URL(url);
            String host = parsedUrl.getHost().toLowerCase();

            // Block localhost and loopback
            if (BLOCKED_DOMAINS.contains(host)) {
                return false;
            }

            // Block private IP ranges
            for (String pattern : PRIVATE_IP_PATTERNS) {
                if (host.matches(pattern)) {
                    return false;
                }
            }

            // Only allow HTTP and HTTPS
            String protocol = parsedUrl.getProtocol().toLowerCase();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                return false;
            }

            return true;

        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * Get validation error message
     * 
     * @param url The URL to validate
     * @return Error message or null if valid
     */
    public String getValidationError(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "URL cannot be empty";
        }

        if (url.length() > MAX_URL_LENGTH) {
            return "URL exceeds maximum length of " + MAX_URL_LENGTH + " characters";
        }

        if (!URL_PATTERN.matcher(url).matches()) {
            return "Invalid URL format. Must start with http:// or https://";
        }

        try {
            URL parsedUrl = new URL(url);
            String host = parsedUrl.getHost().toLowerCase();

            if (BLOCKED_DOMAINS.contains(host)) {
                return "Localhost and loopback addresses are not allowed";
            }

            for (String pattern : PRIVATE_IP_PATTERNS) {
                if (host.matches(pattern)) {
                    return "Private IP addresses are not allowed";
                }
            }

            String protocol = parsedUrl.getProtocol().toLowerCase();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                return "Only HTTP and HTTPS protocols are allowed";
            }

        } catch (MalformedURLException e) {
            return "Malformed URL: " + e.getMessage();
        }

        return null;
    }
}
