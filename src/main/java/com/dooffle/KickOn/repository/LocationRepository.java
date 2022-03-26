package com.dooffle.KickOn.repository;

import com.dooffle.KickOn.data.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
}
