package com.dooffle.KickOn.fcm.service;

import com.dooffle.KickOn.dto.FeedDto;
import com.dooffle.KickOn.models.PushNotificationRequest;
import com.dooffle.KickOn.services.DeviceService;
import com.dooffle.KickOn.utils.CommonUtil;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class PushNotificationService {

    private final Logger logger = LoggerFactory.getLogger(PushNotificationService.class);

    private final FCMService fcmService;

    @Autowired
    private DeviceService deviceService;

    public PushNotificationService(FCMService fcmService) {
        this.fcmService = fcmService;
    }


    public void sendPushNotificationToToken(PushNotificationRequest request) {
        HashMap<String,String> data = new HashMap<>();
        data.put("Key","value");
        request.setData(data);
        try {
            if (request.getToken() == null) {
                Set<String> devices = deviceService.getDeviceIdUsingUserId(request.getUserId());
                for (String x : devices) {
                    request.setToken(x);
                    fcmService.sendMessageToToken(request);
                }

            } else {
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

    public TopicManagementResponse subscribeToTopic(String topicName) {
        String validatedTopic= topicName.replaceAll(" ","_");
        Set<String> device = deviceService.getDeviceIdUsingUserId(CommonUtil.getLoggedInUserId());
        try {
            return fcmService.subscribeToTopic(device.stream().collect(Collectors.toList()), validatedTopic);
        } catch (FirebaseMessagingException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public void sendPushNotificationToTopic(PushNotificationRequest request) {
        try {

                if(request.getTopic()!=null){
                    request.setTopic(request.getTopic().replaceAll(" ",""));
                    fcmService.sendMessageToTopic(request);
                }else {
                    fcmService.sendMessageToCondition(request);
                }


        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void sendNotificationToDevices(List<String> deviceIds) {
        try {

            fcmService.sendMessageToDevices(deviceIds);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}