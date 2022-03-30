package com.dooffle.KickOn.fcm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private PushNotificationService pushNotificationService;


}