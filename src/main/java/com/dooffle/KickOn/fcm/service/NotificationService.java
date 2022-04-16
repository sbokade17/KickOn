package com.dooffle.KickOn.fcm.service;

import com.dooffle.KickOn.dto.EventDto;

public interface NotificationService {

    void sendNotificationToTopic(EventDto responseDto);
}