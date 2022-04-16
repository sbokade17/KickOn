package com.dooffle.KickOn.fcm.service;

import com.dooffle.KickOn.dto.EventDto;
import com.dooffle.KickOn.models.PushNotificationRequest;
import com.dooffle.KickOn.services.SearchService;
import com.dooffle.KickOn.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private SearchService searchService;


    @Override
    public void sendNotificationToTopic(EventDto responseDto) {
        if(responseDto.getType().equals(Constants.TRIAL) || responseDto.getType().equals(Constants.TOURNAMENT)){

            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTopic(responseDto.getLocation());
            notificationRequest.setMessage("Tap to see details");
            notificationRequest.setTitle("New "+responseDto.getType()+" posted in your area");

            ObjectMapper mapObject = new ObjectMapper();
            Map < String, String > mapObj = mapObject.convertValue(searchService.getSingleSearch(responseDto.getType(),responseDto.getEventId()), Map.class);
            notificationRequest.setData(mapObj);
            pushNotificationService.sendPushNotificationToTopic(notificationRequest);
        }

    }
}