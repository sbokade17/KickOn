package com.dooffle.KickOn.repository;

import com.dooffle.KickOn.data.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {
    Optional<EventEntity> findByEventIdAndCreatedBy(Long eventId, String loggedInUserId);

    void deleteByEventIdAndCreatedBy(Long eventId, String loggedInUserId);
}
