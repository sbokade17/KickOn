package com.dooffle.KickOn.services;

import com.dooffle.KickOn.dto.EventDto;

import java.util.List;
import java.util.Map;

public interface EventService {
    EventDto createEvent(EventDto eventDto);

    List<EventDto> getAllEvents(String search, int start, int end);

    void deleteById(Long eventId);

    EventDto getEventById(Long eventId);

    void addLike(Long id);

    EventDto patchEvent(Long eventId, Map<String, Object> patchObject);

    void removeLike(Long id);

    void addShare(Long id);

    List<Map> getAllEventsWithNews(int start, int end);
}
