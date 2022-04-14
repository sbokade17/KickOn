package com.dooffle.KickOn.repository;

import com.dooffle.KickOn.data.FeedEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface FeedRepository extends JpaRepository<FeedEntity, Long> {
    List<FeedEntity> findAllByLinkIn(Set<String> urlSet);

    List<FeedEntity> findByLinkIn(Set<String> urlSet, Pageable sortedByDate);

    List<FeedEntity> findByLinkInAndKeywordsContaining(Set<String> urlSet, String category, Pageable sortedByDate);
}
