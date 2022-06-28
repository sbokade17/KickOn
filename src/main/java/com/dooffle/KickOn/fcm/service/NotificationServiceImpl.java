package com.dooffle.KickOn.fcm.service;

import com.dooffle.KickOn.dto.EventDto;
import com.dooffle.KickOn.dto.FeedDto;
import com.dooffle.KickOn.dto.SearchDto;
import com.dooffle.KickOn.models.PushNotificationRequest;
import com.dooffle.KickOn.services.LocationService;
import com.dooffle.KickOn.services.SearchService;
import com.dooffle.KickOn.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private LocationService locationService;

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public void sendNotificationToTopic(EventDto responseDto) {
        if (responseDto.getType().equals(Constants.TRIAL) || responseDto.getType().equals(Constants.TOURNAMENT)) {

            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setTopic(locationService.findById(Long.parseLong(responseDto.getLocation())).getLocName());
            notificationRequest.setMessage("Tap to see details");
            notificationRequest.setTitle("New " + responseDto.getType() + " posted in your area");

            ObjectMapper mapObject = new ObjectMapper();

            Map<String, Object> mapObj = new HashMap<>();
            SearchDto data = searchService.getSingleSearch(responseDto.getType(), responseDto.getEventId());
            mapObj.put("name", data.getName());
            mapObj.put("type", data.getType());
            mapObj.put("id", data.getId());
            mapObj.put("image", data.getImage() == null ? "" : data.getImage());
            mapObj.put("description", responseDto.getDescription());
            String strdate = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (data.getDate() != null) {
                strdate = sdf.format(data.getDate().getTime());
            }
            mapObj.put("date", strdate);
            mapObj.put("link", data.getLink());
            notificationRequest.setData(mapObj);
            pushNotificationService.sendPushNotificationToTopic(notificationRequest);
        }

    }

    @Override
    public void sendNewFeedNotification(List<FeedDto> feedsToBeAddedToDB) {


        feedsToBeAddedToDB.forEach(x -> {

            String[] keywords = x.getKeywords().split(",");
            StringBuilder sb = new StringBuilder();
            Arrays.stream(keywords).forEach(k -> {
                sb.append("'").append(k).append("' in topics  || ");
            });
            PushNotificationRequest request = new PushNotificationRequest();
            String condition = sb.substring(0, sb.length() - 3);
            //'stock-GOOG' in topics || 'industry-tech' in topics
            request.setCondition(condition);
            SearchDto data = searchService.getSingleSearch(Constants.FEED, x.getFeedId());
            ObjectMapper mapObject = new ObjectMapper();
            Map<String, String> mapObj = new HashMap<>();
            mapObj.put("name", data.getName());
            mapObj.put("type", data.getType());
            mapObj.put("id", data.getId());
            mapObj.put("image", data.getImage() == null ? "" : data.getImage());
            mapObj.put("description", x.getTitle());
            String strdate = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (data.getDate() != null) {
                strdate = sdf.format(data.getDate().getTime());
            }
            mapObj.put("date", strdate);
            mapObj.put("link", data.getLink());
            request.setData(mapObj);
            request.setMessage("Tap to see details");
            request.setTitle(x.getTitle());
            pushNotificationService.sendPushNotificationToTopic(request);

        });

    }

    @Override
    public void subscribeCurrentUserToTopic(String k) {
        pushNotificationService.subscribeToTopic(k);
    }
}