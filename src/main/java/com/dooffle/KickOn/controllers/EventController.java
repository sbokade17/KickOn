package com.dooffle.KickOn.controllers;

import com.dooffle.KickOn.dto.EventDto;
import com.dooffle.KickOn.dto.StatusDto;
import com.dooffle.KickOn.dto.UserDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.models.GetUserDetailsResponseModel;
import com.dooffle.KickOn.services.EventService;
import com.dooffle.KickOn.utils.CommonUtil;
import com.dooffle.KickOn.utils.Constants;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public ResponseEntity<List<EventDto>> getAllEvents(@RequestParam(value = "search", required = false) String search){

        List<EventDto> eventDtos = eventService.getAllEvents(search);
        return ResponseEntity.status(HttpStatus.OK).body(eventDtos);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<StatusDto> deleteFileById(@PathVariable("eventId") Long eventId) throws IOException {
        try{

            eventService.deleteById(eventId);
            return ResponseEntity.status(HttpStatus.OK).body(new StatusDto(Constants.DELETED));
        }catch (RuntimeException e){
            throw new CustomAppException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }


}
