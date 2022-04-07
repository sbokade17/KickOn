package com.dooffle.KickOn.repository;

import com.dooffle.KickOn.data.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    Optional<LikeEntity> findByFeedIdAndUserIdAndType(Long feedId, String loggedInUserId, String feed);

}
