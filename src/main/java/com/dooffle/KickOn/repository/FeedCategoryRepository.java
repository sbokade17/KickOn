package com.dooffle.KickOn.repository;

import com.dooffle.KickOn.data.FeedCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedCategoryRepository extends JpaRepository<FeedCategoryEntity, Long> {
    FeedCategoryEntity findByCategoryName(String category);
}
