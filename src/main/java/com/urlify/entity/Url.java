package com.urlify.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Document(collection = "urls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Url {

    @Id
    private String id;

    private String originalUrl;

    @Indexed(unique = true)
    private String shortCode;

    private String userId;

    private Long clicks = 0L;

    @Indexed
    private LocalDateTime expiresAt;

    @CreatedDate
    private LocalDateTime createdAt;

    public void incrementClicks() {
        this.clicks++;
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
