package com.urlify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlResponse {

    private String id;
    private String originalUrl;
    private String shortCode;
    private String shortUrl;
    private Long clicks;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
