package com.dooffle.KickOn.controllers;

import com.dooffle.KickOn.dto.EventDto;
import com.dooffle.KickOn.dto.UserDto;
import com.dooffle.KickOn.models.GetUserDetailsResponseModel;
import com.dooffle.KickOn.services.EventService;
import com.dooffle.KickOn.utils.CommonUtil;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    EventService eventService;

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody EventDto eventDto) {

        EventDto eventResponse = eventService.createEvent(eventDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventResponse);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<EventDto>> getAllEvents(){
        List<EventDto> eventDtos = eventService.getAllEvents();
        return ResponseEntity.status(HttpStatus.OK).body(eventDtos);
    }
}
