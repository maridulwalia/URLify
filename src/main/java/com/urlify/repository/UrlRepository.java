package com.urlify.repository;

import com.urlify.entity.Url;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<Url, String> {

    Optional<Url> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    List<Url> findByUserId(String userId);

    Page<Url> findByUserId(String userId, Pageable pageable);

    @Query("{ 'expiresAt': { $ne: null, $lt: ?0 } }")
    List<Url> findExpiredUrls(LocalDateTime now);

    void deleteByShortCode(String shortCode);
}
