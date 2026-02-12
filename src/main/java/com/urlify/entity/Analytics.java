package com.urlify.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Analytics {

    @Id
    private String id;

    @Indexed
    private String shortCode;

    @CreatedDate
    @Indexed
    private LocalDateTime timestamp;

    private String ipAddress;

    private String userAgent;

    private String referer;
}
