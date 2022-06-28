package com.dooffle.KickOn.services;

import com.dooffle.KickOn.dto.SearchDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.utils.Constants;
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
import java.util.List;

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
                    "\tLOCATION,\n" +
                    "\t'' AS LINK,\n" +
                    "\tCAST(\n" +
                    "\t\t\t\t\t\t\t(SELECT MIN(ID)\n" +
                    "\t\t\t\t\t\t\t\tFROM FILE_TABLE\n" +
                    "\t\t\t\t\t\t\t\tWHERE FILE_TABLE.EVENT_ID = et.EVENT_ID) AS CHARACTER VARYING(255)) AS IMAGE, DATE\n" +
                    "FROM EVENT_TABLE as et \n" +
                    "WHERE LOWER(NAME) like LOWER('%"+search+"%')\n" +
                    "UNION\n" +
                    "SELECT NAME,\n" +
                    "\tTYPE,\n" +
                    "\tEVENT_ID AS ID,\n" +
                    "\tLOCATION,\n" +
                    "    '' AS LINK,\n" +
                    "\tCAST(\n" +
                    "\t\t\t\t\t\t\t(SELECT MIN(ID)\n" +
                    "\t\t\t\t\t\t\t\tFROM FILE_TABLE\n" +
                    "\t\t\t\t\t\t\t\tWHERE FILE_TABLE.EVENT_ID = et.EVENT_ID) AS CHARACTER VARYING(255)) AS IMAGE, DATE\n" +
                    "FROM EVENT_TABLE as et \n" +
                    "WHERE LOWER(DESCRIPTION) like LOWER('%"+search+"%')\n" +
                    "UNION\n" +
                    "SELECT TITLE AS NAME,\n" +
                    "\t'Feed' AS TYPE,\n" +
                    "\tFEED_ID AS ID,\n" +
                    "\t'' AS LOCATION,\n" +
                    "\tLINK AS LINK,\n" +
                    "\tIMAGE_URL AS IMAGE, DATE\n" +
                    "FROM FEED_TABLE \n" +
                    "WHERE LOWER(TITLE) like LOWER('%"+search+"%')\n" +
                    " ORDER BY ID DESC" ;

            Query query = entityManager.createNativeQuery(
                    queryString
            );
            query.unwrap(SQLQuery.class)
                    .addScalar("name", StringType.INSTANCE)
                    .addScalar("type", StringType.INSTANCE)
                    .addScalar("id", StringType.INSTANCE)
                    .addScalar("image", StringType.INSTANCE)
                    .addScalar("date", CalendarType.INSTANCE)
                    .addScalar("link", StringType.INSTANCE)
                    .addScalar("location", StringType.INSTANCE)
                    .setResultTransformer(Transformers.aliasToBean(SearchDto.class));

            return query.getResultList();


        } catch (RuntimeException e) {
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, e.getLocalizedMessage());
        }
    }

    @Override
    public SearchDto getSingleSearch(String type, Long id) {
        try {
            String queryString= "SELECT NAME,\n" +
                    "\tTYPE,\n" +
                    "\tEVENT_ID AS ID,\n" +
                    "\tLOCATION,\n" +
                    "\t'' AS LINK,\n" +
                    "\tCAST(\n" +
                    "\t\t\t\t\t\t\t(SELECT MIN(ID)\n" +
                    "\t\t\t\t\t\t\t\tFROM FILE_TABLE\n" +
                    "\t\t\t\t\t\t\t\tWHERE FILE_TABLE.EVENT_ID = et.EVENT_ID) AS CHARACTER VARYING(255)) AS IMAGE, DATE\n" +
                    "FROM EVENT_TABLE as et \n" +
                    "WHERE type = '"+type+"' AND EVENT_ID ="+id+
                    " UNION\n" +
                    "SELECT NAME,\n" +
                    "\tTYPE,\n" +
                    "\tEVENT_ID AS ID,\n" +
                    "\tLOCATION,\n" +
                    "    '' AS LINK,\n" +
                    "\tCAST(\n" +
                    "\t\t\t\t\t\t\t(SELECT MIN(ID)\n" +
                    "\t\t\t\t\t\t\t\tFROM FILE_TABLE\n" +
                    "\t\t\t\t\t\t\t\tWHERE FILE_TABLE.EVENT_ID = et.EVENT_ID) AS CHARACTER VARYING(255)) AS IMAGE, DATE\n" +
                    "FROM EVENT_TABLE  as et \n" +
                    "WHERE type = '"+type+"' AND EVENT_ID ="+id+

                    " ORDER BY ID DESC" ;

            Query query = entityManager.createNativeQuery(
                    queryString
            );
            query.unwrap(SQLQuery.class)
                    .addScalar("name", StringType.INSTANCE)
                    .addScalar("type", StringType.INSTANCE)
                    .addScalar("id", StringType.INSTANCE)
                    .addScalar("image", StringType.INSTANCE)
                    .addScalar("date", CalendarType.INSTANCE)
                    .addScalar("link", StringType.INSTANCE)
                    .addScalar("location", StringType.INSTANCE)
                    .setResultTransformer(Transformers.aliasToBean(SearchDto.class));

            List<SearchDto> result = query.getResultList();
            return result.get(0);


        } catch (RuntimeException e) {
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, e.getLocalizedMessage());
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
