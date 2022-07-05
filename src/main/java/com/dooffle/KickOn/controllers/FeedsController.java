package com.dooffle.KickOn.controllers;

import com.dooffle.KickOn.dto.CategoriesDto;
import com.dooffle.KickOn.dto.FeedDto;
import com.dooffle.KickOn.dto.StatusDto;
import com.dooffle.KickOn.services.FeedService;
import com.dooffle.KickOn.utils.Constants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class FeedsController {

    @Autowired
    FeedService feedService;

    @Value("${app.google.clientId}")
    private String feedsUrl;


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

    @GetMapping(value = "/categories")
    public ResponseEntity<List<CategoriesDto>> getCategories(){
        List<CategoriesDto> allCategories = feedService.getAllCategories();
        return ResponseEntity.status(HttpStatus.OK).body(allCategories);
    }

    @GetMapping
    public List<FeedDto> getRssFeed(@RequestParam(value = "category", required = false, defaultValue = "All") String category,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "start", required = false,  defaultValue = "0") int start,
                                    @RequestParam(value = "end", required = false,  defaultValue = "50") int end) throws IOException {


        try {
            return feedService.addAndGetFeeds(keyword, start, end, category);
        }  catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }


}
