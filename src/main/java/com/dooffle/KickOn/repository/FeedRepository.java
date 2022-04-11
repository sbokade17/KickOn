package com.dooffle.KickOn.repository;

import com.dooffle.KickOn.data.FeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface FeedRepository extends JpaRepository<FeedEntity, Long> {
    List<FeedEntity> findAllByLinkIn(Set<String> urlSet);

    List<FeedEntity> findTop50ByLinkInOrderByDateDesc(Set<String> urlSet);

    List<FeedEntity> findTop50ByLinkInAndKeywordsContainingOrderByDateDesc(Set<String> urlSet, String category);
}
