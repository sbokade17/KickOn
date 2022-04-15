package com.dooffle.KickOn.fcm;

import com.dooffle.KickOn.dto.StatusDto;
import com.dooffle.KickOn.fcm.service.PushNotificationService;
import com.dooffle.KickOn.models.PushNotificationRequest;
import com.dooffle.KickOn.models.PushNotificationResponse;
import com.dooffle.KickOn.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PushNotificationController {
	
	@Autowired
    private PushNotificationService pushNotificationService;

    
    @PostMapping("/notification/token")
    public ResponseEntity sendTokenNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationToToken(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/notification/topic")
    public ResponseEntity sendTopicNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationToToken(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @GetMapping("/notification/chat/{userId}")
    private ResponseEntity<StatusDto> sendNotification(@PathVariable("userId") String userId){
        pushNotificationService.sendNotificationTo(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new StatusDto(Constants.SUCCESS));
    }
    
}