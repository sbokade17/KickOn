package com.dooffle.KickOn.repository;

import com.dooffle.KickOn.data.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Calendar;
import java.util.Optional;
import java.util.stream.Collectors;

public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {
    Optional<EventEntity> findByEventIdAndCreatedBy(Long eventId, String loggedInUserId);

    int deleteByEventIdAndCreatedBy(Long eventId, String loggedInUserId);

    Page findAllByTypeAndDateAfter(String tournament, Calendar instance, Pageable sortedByEventId);

    Page findAllByTypeAndDateBefore(String tournament, Calendar instance, Pageable sortedByEventId);

    int deleteByEventId(Long eventId);
}
