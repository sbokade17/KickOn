package com.dooffle.KickOn.services;

import com.dooffle.KickOn.data.FeedEntity;
import com.dooffle.KickOn.dto.FeedDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.repository.FeedRepository;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FeedServiceImpl implements FeedService{

    @Autowired
    FeedRepository feedRepository;

    @Override
    public List<FeedDto> addAndGetFeeds(List<FeedDto> feedDtos) {
        try {
            Set<String> urlSet = feedDtos.stream().map(x -> x.getLink()).collect(Collectors.toSet());
            List<FeedEntity> feeds =feedRepository.findAllByLinkIn(urlSet);
            Set<String> existingFeeds = feeds.stream().map(x -> x.getLink()).collect(Collectors.toSet());
            List<FeedDto> feedsToBeAddedToDB = feedDtos.stream().filter(x -> !existingFeeds.contains(x.getLink())).collect(Collectors.toList());
            feedRepository.saveAll(ObjectMapperUtils.mapAll(feedsToBeAddedToDB, FeedEntity.class));
            return ObjectMapperUtils.mapAll(feedRepository.findAllByLinkInOrderByDateDesc(urlSet), FeedDto.class);
        }catch (RuntimeException re){
            re.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void addLike(Long id) {
        try{
            FeedEntity feedEntity = feedRepository.findById(id).get();
            feedEntity.setLikes(feedEntity.getLikes()+1);
            feedRepository.save(feedEntity);
        }catch (RuntimeException re){
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Error while adding like");
        }

    }

    @Override
    public FeedDto getFeedById(Long id) {
        try{
            FeedEntity feedEntity = feedRepository.findById(id).get();
            return ObjectMapperUtils.map(feedEntity, FeedDto.class);
        }catch (RuntimeException re){
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Feed not found");
        }
    }
}
