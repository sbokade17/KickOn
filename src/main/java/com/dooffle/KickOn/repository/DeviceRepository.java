package com.dooffle.KickOn.repository;

import com.dooffle.KickOn.data.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {
    List<DeviceEntity> findByUserId(String loggedInUserId);

    DeviceEntity findByUserIdAndDeviceId(String loggedInUserId, String deviceId);
}
