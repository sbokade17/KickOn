package com.dooffle.KickOn.services;

import com.dooffle.KickOn.data.AmenitiesEntity;
import com.dooffle.KickOn.data.EventEntity;
import com.dooffle.KickOn.dto.EventDto;
import com.dooffle.KickOn.repository.AmenitiesRepository;
import com.dooffle.KickOn.repository.EventRepository;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EventServiceImpl implements EventService{

    @Autowired
    EventRepository eventRepository;

    @Autowired
    FileService fileService;

    @Autowired
    AmenitiesRepository amenitiesRepository;

    @Override
    @Transactional
    public EventDto createEvent(EventDto eventDto) {
        EventEntity eventEntity = ObjectMapperUtils.map(eventDto,EventEntity.class);
        eventEntity = eventRepository.save(eventEntity);
        Set<AmenitiesEntity> amenitiesEntities = new HashSet<>(amenitiesRepository.findAllById(eventDto.getAmenitiesIds()));
        eventEntity.setAmenities(amenitiesEntities);
        eventEntity = eventRepository.save(eventEntity);
        fileService.updateEventId(eventDto.getBannerIds(), eventEntity.getEventId());
        EventDto responseDto = ObjectMapperUtils.map(eventEntity, EventDto.class);
        responseDto.setBanners(fileService.getBannersByEventId(eventDto.getEventId()));
        return responseDto;
    }

    @Override
    public List<EventDto> getAllEvents() {

        List<EventEntity> eventEntities = eventRepository.findAll();
        List<EventDto> eventDtos = ObjectMapperUtils.mapAll(eventEntities, EventDto.class);
        eventDtos.forEach(x->{
            x.setBanners(fileService.getBannersByEventId(x.getEventId()));
        });
        return eventDtos;
    }


}
