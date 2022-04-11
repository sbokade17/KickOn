package com.dooffle.KickOn.services;

import com.dooffle.KickOn.data.FeedEntity;
import com.dooffle.KickOn.data.LikeEntity;
import com.dooffle.KickOn.dto.FeedDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.repository.FeedRepository;
import com.dooffle.KickOn.repository.LikeRepository;
import com.dooffle.KickOn.utils.CommonUtil;
import com.dooffle.KickOn.utils.Constants;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedServiceImpl implements FeedService {

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    LikeRepository likeRepository;

    @Override
    public List<FeedDto> addAndGetFeeds(List<FeedDto> feedDtos, String category) {
        try {
            Set<String> urlSet = feedDtos.stream().map(x -> x.getLink()).collect(Collectors.toSet());
            List<FeedEntity> feeds = feedRepository.findAllByLinkIn(urlSet);
            Set<String> existingFeeds = feeds.stream().map(x -> x.getLink()).collect(Collectors.toSet());
            List<FeedDto> feedsToBeAddedToDB = feedDtos.stream().filter(x -> !existingFeeds.contains(x.getLink())).collect(Collectors.toList());
            feedRepository.saveAll(ObjectMapperUtils.mapAll(feedsToBeAddedToDB, FeedEntity.class));
            List<FeedEntity> results = new ArrayList<>();
            if(category==null){
                results = feedRepository.findTop50ByLinkInOrderByDateDesc(urlSet);
            }else {
                results = feedRepository.findTop50ByLinkInAndKeywordsContainingOrderByDateDesc(urlSet,category);
            }

            return ObjectMapperUtils.mapAll(results, FeedDto.class);
        } catch (RuntimeException re) {
            re.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void addLike(Long id) {
        try {


            if(!likeRepository.findByFeedIdAndUserIdAndType(id, CommonUtil.getLoggedInUserId(), Constants.FEED).isPresent()){

                FeedEntity feedEntity = feedRepository.findById(id).get();
                feedEntity.setLikes(feedEntity.getLikes() == null ? 1: feedEntity.getLikes()+ 1);
                feedRepository.save(feedEntity);

                LikeEntity like = new LikeEntity();
                like.setFeedId(feedEntity.getFeedId());
                like.setUserId(CommonUtil.getLoggedInUserId());
                like.setType(Constants.FEED);
                likeRepository.save(like);
            }

        } catch (RuntimeException re) {
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Error while adding like");
        }

    }

    @Override
    public FeedDto getFeedById(Long id) {
        try {
            FeedEntity feedEntity = feedRepository.findById(id).get();
            FeedDto feedDto = ObjectMapperUtils.map(feedEntity, FeedDto.class);
            feedDto.setLiked(likeRepository.findByFeedIdAndUserIdAndType(feedEntity.getFeedId(), CommonUtil.getLoggedInUserId(), Constants.FEED).isPresent());
            return feedDto;

        } catch (RuntimeException re) {
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Feed not found");
        }
    }

    @Override
    public void removeLike(Long id) {
        try {
            Optional<LikeEntity> likeOp = likeRepository.findByFeedIdAndUserIdAndType(id, CommonUtil.getLoggedInUserId(), Constants.FEED);

            if(likeOp.isPresent()){

                FeedEntity feedEntity = feedRepository.findById(id).get();
                feedEntity.setLikes(feedEntity.getLikes()- 1);
                feedRepository.save(feedEntity);
                likeRepository.delete(likeOp.get());
            }

        } catch (RuntimeException re) {
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Error while removing like");
        }
    }
}
