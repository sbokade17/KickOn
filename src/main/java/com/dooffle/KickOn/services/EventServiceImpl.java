package com.dooffle.KickOn.services;

import com.dooffle.KickOn.data.AmenitiesEntity;
import com.dooffle.KickOn.data.EventEntity;
import com.dooffle.KickOn.dto.EventDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.repository.AmenitiesRepository;
import com.dooffle.KickOn.repository.EventRepository;
import com.dooffle.KickOn.rsql.CustomRsqlVisitor;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
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
        if(eventDto.getBannerIds().size()>0){
            fileService.updateEventId(eventDto.getBannerIds(), eventEntity.getEventId());
        }
        EventDto responseDto = ObjectMapperUtils.map(eventEntity, EventDto.class);
        responseDto.setBanners(fileService.getBannersByEventId(eventDto.getEventId()));
        return responseDto;
    }

    @Override
    public List<EventDto> getAllEvents(String search) {
        List<EventEntity> eventEntities = new ArrayList<>();
        if(search!=null){
            Node rootNode = new RSQLParser().parse(search);
            Specification<EventEntity> spec = rootNode.accept(new CustomRsqlVisitor<EventEntity>());
            eventEntities = eventRepository.findAll(spec);
        }else{
            eventEntities = eventRepository.findAll();
        }

        List<EventDto> eventDtos = ObjectMapperUtils.mapAll(eventEntities, EventDto.class);
        eventDtos.forEach(x->{
            x.setBanners(fileService.getBannersByEventId(x.getEventId()));
        });
        return eventDtos;
    }

    @Override
    public void deleteById(Long eventId) {
        try {
            eventRepository.deleteById(eventId);
        } catch (RuntimeException e) {
            throw new CustomAppException(HttpStatus.NOT_FOUND, "Event with Id " + eventId + " not found!");
        }
    }


}
