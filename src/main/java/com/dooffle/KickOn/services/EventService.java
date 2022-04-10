package com.dooffle.KickOn.services;

import com.dooffle.KickOn.dto.EventDto;

import java.util.List;
import java.util.Map;

public interface EventService {
    EventDto createEvent(EventDto eventDto);

    List<EventDto> getAllEvents(String search);

    void deleteById(Long eventId);

    EventDto getEventById(Long eventId);

    void addLike(Long id);

    EventDto patchEvent(Long eventId, Map<String, Object> patchObject);
}
