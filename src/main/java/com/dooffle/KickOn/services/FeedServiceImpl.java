package com.dooffle.KickOn.services;

import com.dooffle.KickOn.data.FeedCategoryEntity;
import com.dooffle.KickOn.data.FeedEntity;
import com.dooffle.KickOn.data.FeedViewEntity;
import com.dooffle.KickOn.data.LikeEntity;
import com.dooffle.KickOn.dto.CategoriesDto;
import com.dooffle.KickOn.dto.FeedDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.fcm.service.NotificationService;
import com.dooffle.KickOn.repository.FeedCategoryRepository;
import com.dooffle.KickOn.repository.FeedRepository;
import com.dooffle.KickOn.repository.FeedViewRepository;
import com.dooffle.KickOn.repository.LikeRepository;
import com.dooffle.KickOn.utils.CommonUtil;
import com.dooffle.KickOn.utils.Constants;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
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


    @Autowired
    FeedViewRepository feedViewRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<FeedDto> addAndGetFeeds(String keyword, int start, int end, String category) {
        List<FeedDto> feedDtos = getFeedsByCategoryName(category);
        try {

            Set<String> urlSet = feedDtos.stream().map(x -> x.getLink()).collect(Collectors.toSet());
            List<FeedEntity> feeds = feedRepository.findAllByLinkIn(urlSet);
            Set<String> existingFeeds = feeds.stream().map(x -> x.getLink()).collect(Collectors.toSet());
            List<FeedDto> feedsToBeAddedToDB = feedDtos.stream().filter(x -> !existingFeeds.contains(x.getLink())).collect(Collectors.toList());
            feedsToBeAddedToDB.forEach(x->{
                x.setKeywords(x.getKeywords()+","+category);
                System.out.println(x.getKeywords());
            });
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
            return new ArrayList<>();
        }

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

            FeedViewEntity view = new FeedViewEntity();
            view.setFeedId(id);
            view.setUserId(CommonUtil.getLoggedInUserId());
            feedViewRepository.save(view);

            String[] keywords = feedEntity.getKeywords().split(",");
            //log.info(String.valueOf(keywords.length));
            Arrays.stream(keywords).forEach(k -> {
                try {
                    String queryString = "select cat_count from (\n" +
                            "\tselect category, count(category) as cat_count from \n" +
                            "\t(SELECT UNNEST(STRING_TO_ARRAY(F.KEYWORDS,',')) AS CATEGORY\n" +
                            "FROM FEED_VIEW_TABLE FV\n" +
                            "JOIN FEED_TABLE F ON F.FEED_ID = FV.FEED_ID\n" +
                            "\twhere FV.USER_ID = '"+CommonUtil.getLoggedInUserId()+"') AS cat\n" +
                            "WHERE category ='"+k+"'\n" +
                            "group by category) as c";
                    Query query = entityManager.createNativeQuery(
                            queryString
                    );
                    //log.info("Query is : {}",queryString);
                    int count = query.getFirstResult();
                    //log.info("Devices size is {}",devices.size());
                    if (count == 3) {
                        notificationService.subscribeCurrentUserToTopic(k);
                    }
                } catch (RuntimeException e) {
                    log.error("Error while subscribing to topic {}", k);
                    log.error(e.getMessage());
                }
            });

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

    @Override
    public List<String> getPersonalisedCategories() {
        String queryString = "select category from (select category, count(category) as cat_count from (SELECT UNNEST(STRING_TO_ARRAY(F.KEYWORDS,',')) AS CATEGORY\n" +
                "FROM FEED_VIEW_TABLE FV\n" +
                "JOIN FEED_TABLE F ON F.FEED_ID = FV.FEED_ID\n" +
                "WHERE FV.USER_ID = '"+CommonUtil.getLoggedInUserId()+"') AS cat\n" +
                "WHERE category not in ('All')\n" +
                "group by category\n" +
                "order by cat_count desc limit 3) as c";
        Query query = entityManager.createNativeQuery(
                queryString
        );
        //log.info("Query is : {}",queryString);
        List<String> devices = query.getResultList();
        return devices;
    }

//    @Override
//    public List<FeedDto> getPersonalisedFeeds() {
//        List<FeedEntity> results = new ArrayList<>();
//        String queryString= "";
//        return ObjectMapperUtils.mapAll(results, FeedDto.class);
//    }


    public List<FeedDto> getFeedsByCategoryName(String category) {
        List<FeedDto> feedDtos = new ArrayList<>();
        String url = getFeedsUrl(category);
        //
        String xmlString = readUrlToString(url);
        JSONObject xmlJSONObj = XML.toJSONObject(xmlString);
        JSONArray postList = xmlJSONObj.getJSONObject("rss").getJSONObject("channel").getJSONArray("item");
        postList.forEach(x->{
            FeedDto feedDto = new FeedDto();
            feedDto.setKeywords(((JSONObject)x).getString("keywords"));
            feedDto.setContent(((JSONObject)x).getString("content:encoded"));
            feedDto.setLikes(0);
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
            try {
                cal.setTime(sdf.parse(((JSONObject)x).getString("pubDate")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            feedDto.setDate(cal);
            feedDto.setTitle(((JSONObject)x).getString("title"));
            feedDto.setLink(((JSONObject)x).getString("link")+"/partners/45111");
            feedDto.setImageUrl(((JSONObject)x).getJSONObject("media:thumbnail").getString("url"));
            feedDtos.add(feedDto);
        });
        return feedDtos;
    }

    public static String readUrlToString(String url) {
        BufferedReader reader = null;
        String result = null;
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setReadTimeout(2 * 1000);
            conn.connect();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            result = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignoreOnClose) { }
            }
        }
        return result;
    }
}
