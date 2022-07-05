package com.dooffle.KickOn.services;


import com.dooffle.KickOn.data.DeviceEntity;
import com.dooffle.KickOn.fcm.service.PushNotificationService;
import com.dooffle.KickOn.repository.DeviceRepository;
import com.dooffle.KickOn.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl implements DeviceService{

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    PushNotificationService notificationService;

    @Override
    public void saveDevice(String userId, String deviceId) {
        DeviceEntity deviceEntity = deviceRepository.findByUserIdAndDeviceId(userId, deviceId);
        if(deviceEntity==null){
            deviceEntity = new DeviceEntity();
            deviceEntity.setUserId(userId);
            deviceEntity.setDeviceId(deviceId);
            deviceRepository.save(deviceEntity);
        }
        try{
            notificationService.subscribeToTopic("ALL");
        }catch (RuntimeException re){

        }

    }

    @Override
    public Set<String> getDeviceIdUsingUserId(String userId) {
        List<DeviceEntity> devices = deviceRepository.findByUserId(userId);
        return devices.stream().map(x->x.getDeviceId()).collect(Collectors.toSet());
    }

    @Override
    public void deleteDevice(String deviceId) {
        DeviceEntity deviceEntity = deviceRepository.findByUserIdAndDeviceId(CommonUtil.getLoggedInUserId(), deviceId);
        deviceRepository.delete(deviceEntity);
    }
}
