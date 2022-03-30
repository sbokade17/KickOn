package com.dooffle.KickOn.services;

import com.dooffle.KickOn.dto.SearchDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.utils.Constants;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService{

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    EventService eventService;

    @Autowired
    FeedService feedService;

    @Override
    public List<SearchDto> findInAll(String search) {

        try {
            String queryString= "select name, type, event_id as id from event_table where lower(name) like lower('%"+search+"%')\n" +
                    "union\n" +
                    "select name, type, event_id as id from event_table where lower(description) like lower('%"+search+"%')\n" +
                    "union\n" +
                    "select title as name, 'Feed' as type, id from feed_table where lower(title) like lower('%"+search+"%')\n" ;

            Query query = entityManager.createNativeQuery(
                    queryString
            );
            query.unwrap(SQLQuery.class)
                    .addScalar("name", StringType.INSTANCE)
                    .addScalar("type", StringType.INSTANCE)
                    .addScalar("id", StringType.INSTANCE)
                    .setResultTransformer(Transformers.aliasToBean(SearchDto.class));

            return query.getResultList();


        } catch (RuntimeException e) {
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Error while searching");
        }
    }

    @Override
    public Object getResultDetail(String type, Long id) {

        Object result;
        switch(type) {
            case Constants.TURF:
            case Constants.TOURNAMENT:
            case Constants.TRIAL:
                result = eventService.getEventById(id);
                break;
            case Constants.FEED:
                result = feedService.getFeedById(id);
            default:
                throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid booking status");
        }
        return result;
    }
}
