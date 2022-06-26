package com.dooffle.KickOn.services;

import com.dooffle.KickOn.data.FeedCategoryEntity;
import com.dooffle.KickOn.data.FeedEntity;
import com.dooffle.KickOn.data.LikeEntity;
import com.dooffle.KickOn.dto.CategoriesDto;
import com.dooffle.KickOn.dto.FeedDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.fcm.service.NotificationService;
import com.dooffle.KickOn.repository.FeedCategoryRepository;
import com.dooffle.KickOn.repository.FeedRepository;
import com.dooffle.KickOn.repository.LikeRepository;
import com.dooffle.KickOn.utils.CommonUtil;
import com.dooffle.KickOn.utils.Constants;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class FeedServiceImpl implements FeedService {

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    FeedCategoryRepository feedCategoryRepository;

    @Autowired
    NotificationService notificationService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FeedDto> addAndGetFeeds(List<FeedDto> feedDtos, String keyword, int start, int end) {
        try {
            Set<String> urlSet = feedDtos.stream().map(x -> x.getLink()).collect(Collectors.toSet());
            List<FeedEntity> feeds = feedRepository.findAllByLinkIn(urlSet);
            Set<String> existingFeeds = feeds.stream().map(x -> x.getLink()).collect(Collectors.toSet());
            List<FeedDto> feedsToBeAddedToDB = feedDtos.stream().filter(x -> !existingFeeds.contains(x.getLink())).collect(Collectors.toList());
            List<FeedEntity> feedsAdded = feedRepository.saveAll(ObjectMapperUtils.mapAll(feedsToBeAddedToDB, FeedEntity.class));
            feedsToBeAddedToDB = ObjectMapperUtils.mapAll(feedsAdded, FeedDto.class);
            try {
                notificationService.sendNewFeedNotification(feedsToBeAddedToDB);
            } catch (RuntimeException re) {
                log.error("Error while sending notification");
                log.error(re.getMessage());
            }
            List<FeedEntity> results;
            Pageable sortedByDate =
                    PageRequest.of(start, end, Sort.by("date").descending());
            if (keyword == null) {
                results = feedRepository.findByLinkIn(urlSet, sortedByDate);
            } else {
                results = feedRepository.findByLinkInAndKeywordsContaining(urlSet, keyword, sortedByDate);
            }
            List<FeedDto> resultsDto = ObjectMapperUtils.mapAll(results, FeedDto.class);
            resultsDto.forEach(x -> {
                x.setLiked(likeRepository.findByFeedIdAndUserIdAndType(x.getFeedId(), CommonUtil.getLoggedInUserId(), Constants.FEED).isPresent());
            });
            return resultsDto;
        } catch (RuntimeException re) {
            re.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void addLike(Long id) {
        try {


            if (!likeRepository.findByFeedIdAndUserIdAndType(id, CommonUtil.getLoggedInUserId(), Constants.FEED).isPresent()) {

                FeedEntity feedEntity = feedRepository.findById(id).get();
                feedEntity.setLikes(feedEntity.getLikes() == null ? 1 : feedEntity.getLikes() + 1);
                feedRepository.save(feedEntity);

                LikeEntity like = new LikeEntity();
                like.setFeedId(feedEntity.getFeedId());
                like.setUserId(CommonUtil.getLoggedInUserId());
                like.setType(Constants.FEED);
                likeRepository.save(like);


                String[] keywords = feedEntity.getKeywords().split(",");
                //log.info(String.valueOf(keywords.length));
                Arrays.stream(keywords).forEach(k -> {
                    try {
                        String queryString = "select user_id from\n" +
                                "(SELECT l.user_id, count(*) as likes, '" + k + "' as keyword\n" +
                                "FROM FEED_TABLE F\n" +
                                "JOIN LIKE_TABLE L ON F.FEED_ID = L.FEED_ID\n" +
                                "where f.keywords like '%" + k + "%'\n" +
                                "group by l.user_id) as table1 where likes= 3";
                        Query query = entityManager.createNativeQuery(
                                queryString
                        );
                        //log.info("Query is : {}",queryString);
                        List<String> devices = query.getResultList();
                        //log.info("Devices size is {}",devices.size());
                        if (devices.size() > 0) {
                            notificationService.subscribeCurrentUserToTopic(k);
                        }
                    } catch (RuntimeException e) {
                        log.error("Error while subscribing to topic {}", k);
                        log.error(e.getMessage());
                    }
                });


            }

        } catch (RuntimeException re) {
            log.error("Error while adding like");
            log.error(re.getMessage());
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

            if (likeOp.isPresent()) {

                FeedEntity feedEntity = feedRepository.findById(id).get();
                feedEntity.setLikes(feedEntity.getLikes() - 1);
                feedRepository.save(feedEntity);
                likeRepository.delete(likeOp.get());
            }

        } catch (RuntimeException re) {
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Error while removing like");
        }
    }

    @Override
    public String getFeedsUrl(String category) {
        FeedCategoryEntity feedCategoryEntity = feedCategoryRepository.findByCategoryName(category);
        return feedCategoryEntity.getCategoryUrl();
    }

    @Override
    public List<CategoriesDto> getAllCategories() {
        return ObjectMapperUtils.mapAll(feedCategoryRepository.findAll(), CategoriesDto.class);
    }

    @Override
    public List<FeedDto> getFeedsByCategoryIn(String category, int start, int end) {
        List<FeedEntity> results;
        Pageable sortedByDate =
                PageRequest.of(start, end, Sort.by("date").descending());

        results = feedRepository.findByKeywordsContaining(category, sortedByDate);

        return ObjectMapperUtils.mapAll(results, FeedDto.class);
    }
}
