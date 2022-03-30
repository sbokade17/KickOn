package com.dooffle.KickOn.fcm.service;

import com.dooffle.KickOn.models.PushNotificationRequest;
import com.dooffle.KickOn.services.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class PushNotificationService {
	
    private Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
    
    private FCMService fcmService;

    @Autowired
    private DeviceService deviceService;

    public PushNotificationService(FCMService fcmService) {
        this.fcmService = fcmService;
    }
    
    
    public void sendPushNotificationToToken(PushNotificationRequest request) {
        try {
            if(request.getToken()==null){
                Set<String> devices= deviceService.getDeviceIdUsingUserId(request.getUserId());
                for (String x : devices) {
                    request.setToken(x);
                    fcmService.sendMessageToToken(request);
                }

            }else {
                fcmService.sendMessageToToken(request);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    public void sendNotificationTo(String userId) {

        PushNotificationRequest request = new PushNotificationRequest();
        request.setMessage("You got a new message!");
        request.setTopic("New Message");
        request.setUserId(userId);
        sendPushNotificationToToken(request);
    }
}