package com.dooffle.KickOn.services;

import java.util.Set;

public interface DeviceService {
    void saveDevice(String userId, String deviceId);

    Set<String> getDeviceIdUsingUserId(String userId);

    void deleteDevice(String deviceId);
}
