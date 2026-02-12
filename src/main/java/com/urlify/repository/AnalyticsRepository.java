package com.urlify.repository;

import com.urlify.entity.Analytics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsRepository extends MongoRepository<Analytics, String> {

    List<Analytics> findByShortCodeOrderByTimestampDesc(String shortCode);

    Long countByShortCode(String shortCode);
}
