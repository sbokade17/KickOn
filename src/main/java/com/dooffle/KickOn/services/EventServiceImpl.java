package com.dooffle.KickOn.services;

import com.dooffle.KickOn.data.AmenitiesEntity;
import com.dooffle.KickOn.data.EventEntity;
import com.dooffle.KickOn.data.LikeEntity;
import com.dooffle.KickOn.dto.EventDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.fcm.service.NotificationService;
import com.dooffle.KickOn.repository.AmenitiesRepository;
import com.dooffle.KickOn.repository.EventRepository;
import com.dooffle.KickOn.repository.LikeRepository;
import com.dooffle.KickOn.rsql.CustomRsqlVisitor;
import com.dooffle.KickOn.utils.CommonUtil;
import com.dooffle.KickOn.utils.Constants;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class EventServiceImpl implements EventService{

    @Autowired
    EventRepository eventRepository;

    @Autowired
    FileService fileService;

    @Autowired
    AmenitiesRepository amenitiesRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    NotificationService notificationService;

    @Override
    @Transactional
    public EventDto createEvent(EventDto eventDto) {
        EventEntity eventEntity = ObjectMapperUtils.map(eventDto,EventEntity.class);
        eventEntity.setCreatedBy(CommonUtil.getLoggedInUserId());
        eventEntity = eventRepository.save(eventEntity);
        if(eventDto.getAmenitiesIds()!=null){
            Set<AmenitiesEntity> amenitiesEntities = new HashSet<>(amenitiesRepository.findAllById(eventDto.getAmenitiesIds()));
            eventEntity.setAmenities(amenitiesEntities);
        }
        eventEntity = eventRepository.save(eventEntity);
        if(eventDto.getBannerIds().size()>0){
            fileService.updateEventId(eventDto.getBannerIds(), eventEntity.getEventId());
        }
        EventDto responseDto = ObjectMapperUtils.map(eventEntity, EventDto.class);
        responseDto.setBanners(fileService.getBannersByEventId(eventDto.getEventId()));
        notificationService.sendNotificationToTopic(responseDto);
        return responseDto;
    }

    @Override
    public List<EventDto> getAllEvents(String search, int start, int end) {
        List<EventEntity> eventEntities = new ArrayList<>();
        Pageable sortedByEventId =
                PageRequest.of(start, end, Sort.by("eventId").descending());
        if(search!=null){
            Node rootNode = new RSQLParser().parse(search);
            Specification<EventEntity> spec = rootNode.accept(new CustomRsqlVisitor<EventEntity>());
            eventEntities = eventRepository.findAll(spec, sortedByEventId).toList();
        }else{
            eventEntities = eventRepository.findAll(sortedByEventId).toList();
        }

        List<EventDto> eventDtos = ObjectMapperUtils.mapAll(eventEntities, EventDto.class);
        eventDtos.forEach(x->{
            x.setBanners(fileService.getBannersByEventId(x.getEventId()));
            x.setLiked(likeRepository.findByFeedIdAndUserIdAndType(x.getEventId(), CommonUtil.getLoggedInUserId(), Constants.EVENT).isPresent());
        });
        return eventDtos;
    }

    @Override
    public void deleteById(Long eventId) {
        try {
            eventRepository.deleteByEventIdAndCreatedBy(eventId, CommonUtil.getLoggedInUserId());
        } catch (RuntimeException e) {
            throw new CustomAppException(HttpStatus.NOT_FOUND, "Event with Id " + eventId + " not found!");
        }
    }

    @Override
    public EventDto getEventById(Long eventId) {
        try {
            EventEntity eventEntity = eventRepository.findById(eventId).get();
            EventDto responseDto = ObjectMapperUtils.map(eventEntity, EventDto.class);
            responseDto.setBanners(fileService.getBannersByEventId(eventEntity.getEventId()));
            responseDto.setLiked(likeRepository.findByFeedIdAndUserIdAndType(eventEntity.getEventId(), CommonUtil.getLoggedInUserId(), Constants.EVENT).isPresent());
            return responseDto;
        }catch (RuntimeException e) {
            throw new CustomAppException(HttpStatus.NOT_FOUND, "Event with Id " + eventId + " not found!");
        }

    }

    @Override
    public void addLike(Long id) {
        try {


            if(!likeRepository.findByFeedIdAndUserIdAndType(id, CommonUtil.getLoggedInUserId(), Constants.EVENT).isPresent()){
                EventEntity eventEntity = eventRepository.findById(id).get();
                eventEntity.setLikes(eventEntity.getLikes() == null ? 1 : eventEntity.getLikes()+ 1);
                eventRepository.save(eventEntity);

                LikeEntity like = new LikeEntity();
                like.setFeedId(eventEntity.getEventId());
                like.setUserId(CommonUtil.getLoggedInUserId());
                like.setType(Constants.EVENT);
                likeRepository.save(like);
            }

        } catch (RuntimeException re) {
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Error while adding like");
        }
    }

    @Override
    public EventDto patchEvent(Long eventId, Map<String, Object> patchObject) {
        try {
            EventEntity eventEntity = eventRepository.findByEventIdAndCreatedBy(eventId,CommonUtil.getLoggedInUserId()).get();
            eventEntity = ObjectMapperUtils.map(patchObject, eventEntity);
            EventDto eventDto = ObjectMapperUtils.map(eventRepository.save(eventEntity), EventDto.class);
            eventDto = ObjectMapperUtils.map(patchObject, eventDto);
            if(eventDto.getBannerIds()!=null && eventDto.getBannerIds().size()>0){
                fileService.updateEventId(eventDto.getBannerIds(), eventEntity.getEventId());
            }
            EventDto responseDto = ObjectMapperUtils.map(eventEntity, EventDto.class);
            responseDto.setBanners(fileService.getBannersByEventId(eventDto.getEventId()));
            return responseDto;
        } catch (EmptyResultDataAccessException e) {
            throw new CustomAppException(HttpStatus.NOT_FOUND, "Event with id " + eventId + " not found.");
        }
    }

    @Override
    public void removeLike(Long id) {
        try {
            Optional<LikeEntity> likeOpt = likeRepository.findByFeedIdAndUserIdAndType(id, CommonUtil.getLoggedInUserId(), Constants.EVENT);

            if(likeOpt.isPresent()){
                EventEntity eventEntity = eventRepository.findById(id).get();
                eventEntity.setLikes(eventEntity.getLikes()- 1);
                eventRepository.save(eventEntity);
                likeRepository.delete(likeOpt.get());
            }

        } catch (RuntimeException re) {
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Error while adding like");
        }
    }

    @Override
    public void addShare(Long id) {

    }


}
