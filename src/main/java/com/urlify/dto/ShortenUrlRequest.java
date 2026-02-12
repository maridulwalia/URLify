package com.urlify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortenUrlRequest {

    @NotBlank(message = "URL is required")
    private String url;

    private String customAlias;

    @Positive(message = "Expiry hours must be positive")
    private Integer expiryHours;
}
