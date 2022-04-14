package com.dooffle.KickOn.controllers;

import com.dooffle.KickOn.dto.FeedDto;
import com.dooffle.KickOn.dto.StatusDto;
import com.dooffle.KickOn.services.FeedService;
import com.dooffle.KickOn.utils.Constants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/feeds")
public class RssController {

    @Autowired
    FeedService feedService;

    @PostMapping(value = "/like/{id}")
    public ResponseEntity<StatusDto> addLike(@PathVariable("id") Long id){
        feedService.addLike(id);
        return ResponseEntity.status(HttpStatus.OK).body(new StatusDto(Constants.SUCCESS));
    }

    @PostMapping(value = "/dislike/{id}")
    public ResponseEntity<StatusDto> removeLike(@PathVariable("id") Long id){
        feedService.removeLike(id);
        return ResponseEntity.status(HttpStatus.OK).body(new StatusDto(Constants.SUCCESS));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<FeedDto> getFeedById(@PathVariable("id") Long id){
        FeedDto feedDto = feedService.getFeedById(id);
        return ResponseEntity.status(HttpStatus.OK).body(feedDto);
    }

    @GetMapping
    public List<FeedDto> getRssFeed(@RequestParam(value = "category", required = false) String category,
                                    @RequestParam(value = "start", required = false,  defaultValue = "0") int start,
                                    @RequestParam(value = "end", required = false,  defaultValue = "50") int end) throws IOException {
        List<FeedDto> feedDtos = new ArrayList<>();

        try {
            String url = "https://content.voltax.io/feed/01fs2kwrxmxxw2";
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
            return feedService.addAndGetFeeds(feedDtos, category, start, end);
        }  catch (Exception e) {
            e.printStackTrace();
        }
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
