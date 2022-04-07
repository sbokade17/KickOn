package com.dooffle.KickOn.services;

import com.dooffle.KickOn.dto.SearchDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.utils.Constants;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.CalendarType;
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
            String queryString= "SELECT NAME,\n" +
                    "\tTYPE,\n" +
                    "\tEVENT_ID AS ID,\n" +
                    "\n" +
                    "\t\tCAST( (SELECT MIN(ID)\n" +
                    "\t\tFROM FILE_TABLE\n" +
                    "\t\tWHERE FILE_TABLE.EVENT_ID = EVENT_ID) as CHARACTER VARYING(255) ) AS IMAGE, DATE\n" +
                    "FROM EVENT_TABLE\n" +
                    "WHERE LOWER(NAME) like LOWER('%"+search+"%')\n" +
                    "UNION\n" +
                    "SELECT NAME,\n" +
                    "\tTYPE,\n" +
                    "\tEVENT_ID AS ID,\n" +
                    "\n" +
                    "\t\tCAST( (SELECT MIN(ID)\n" +
                    "\t\tFROM FILE_TABLE\n" +
                    "\t\tWHERE FILE_TABLE.EVENT_ID = EVENT_ID) as CHARACTER VARYING(255) ) AS IMAGE, DATE\n" +
                    "FROM EVENT_TABLE\n" +
                    "WHERE LOWER(DESCRIPTION) like LOWER('%"+search+"%')\n" +
                    "UNION\n" +
                    "SELECT TITLE AS NAME,\n" +
                    "\t'Feed' AS TYPE,\n" +
                    "\tFEED_ID AS ID,\n" +
                    "\tIMAGE_URL AS IMAGE, DATE\n" +
                    "FROM FEED_TABLE\n" +
                    "WHERE LOWER(TITLE) like LOWER('%"+search+"%')" ;
            Query query = entityManager.createNativeQuery(
                    queryString
            );
            query.unwrap(SQLQuery.class)
                    .addScalar("name", StringType.INSTANCE)
                    .addScalar("type", StringType.INSTANCE)
                    .addScalar("id", StringType.INSTANCE)
                    .addScalar("image", StringType.INSTANCE)
                    .addScalar("date", CalendarType.INSTANCE)
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
