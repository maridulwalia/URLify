package com.urlify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {

    private String shortCode;
    private String originalUrl;
    private Long totalClicks;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private List<ClickDetail> recentClicks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClickDetail {
        private LocalDateTime timestamp;
        private String ipAddress;
        private String userAgent;
        private String referer;
    }
}
