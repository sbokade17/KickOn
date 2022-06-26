package com.dooffle.KickOn.fcm.service;

import com.dooffle.KickOn.dto.EventDto;
import com.dooffle.KickOn.dto.FeedDto;

import java.util.List;

public interface NotificationService {

    void sendNotificationToTopic(EventDto responseDto);

    void sendNewFeedNotification(List<FeedDto> feedsToBeAddedToDB);

    void subscribeCurrentUserToTopic(String k);
}