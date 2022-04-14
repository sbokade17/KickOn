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
import java.util.Map;

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

    @PostMapping(value = "/like/{id}")
    public ResponseEntity<StatusDto> addLike(@PathVariable("id") Long id){
        eventService.addLike(id);
        return ResponseEntity.status(HttpStatus.OK).body(new StatusDto(Constants.SUCCESS));
    }

    @PostMapping(value = "/share/{id}")
    public ResponseEntity<StatusDto> addShare(@PathVariable("id") Long id){
        eventService.addShare(id);
        return ResponseEntity.status(HttpStatus.OK).body(new StatusDto(Constants.SUCCESS));
    }

    @PostMapping(value = "/dislike/{id}")
    public ResponseEntity<StatusDto> removeLike(@PathVariable("id") Long id){
        eventService.removeLike(id);
        return ResponseEntity.status(HttpStatus.OK).body(new StatusDto(Constants.SUCCESS));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<EventDto>> getAllEvents(@RequestParam(value = "search", required = false) String search,
                                                       @RequestParam(value = "start", required = false,  defaultValue = "0") int start,
                                                       @RequestParam(value = "end", required = false,  defaultValue = "50") int end){

        List<EventDto> eventDtos = eventService.getAllEvents(search,start,end);
        return ResponseEntity.status(HttpStatus.OK).body(eventDtos);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEventById(@PathVariable("eventId") Long eventId) throws IOException {
        try{

            EventDto eventDto = eventService.getEventById(eventId);
            return ResponseEntity.status(HttpStatus.OK).body(eventDto);
        }catch (RuntimeException e){
            throw new CustomAppException(HttpStatus.NOT_FOUND, e.getMessage());
        }

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

    @PatchMapping(value= "/{eventId}")
    public ResponseEntity<EventDto> patchPet(@PathVariable("eventId") Long eventId, @RequestBody Map<String,Object> patchObject){

        EventDto eventDto=eventService.patchEvent(eventId, patchObject);
        return ResponseEntity.status(HttpStatus.OK).body(eventDto);
    }



}
