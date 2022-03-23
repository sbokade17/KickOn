package com.dooffle.KickOn.repository;

import com.dooffle.KickOn.data.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
}
