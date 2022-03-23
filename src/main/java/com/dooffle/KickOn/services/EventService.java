package com.dooffle.KickOn.services;

import com.dooffle.KickOn.dto.EventDto;

import java.util.List;

public interface EventService {
    EventDto createEvent(EventDto eventDto);

    List<EventDto> getAllEvents();
}
