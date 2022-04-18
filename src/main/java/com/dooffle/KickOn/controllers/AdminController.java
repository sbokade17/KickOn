package com.dooffle.KickOn.controllers;

import com.dooffle.KickOn.data.AmenitiesEntity;
import com.dooffle.KickOn.data.FeedCategoryEntity;
import com.dooffle.KickOn.dto.StatusDto;
import com.dooffle.KickOn.repository.AmenitiesRepository;
import com.dooffle.KickOn.repository.FeedCategoryRepository;
import com.dooffle.KickOn.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private FeedCategoryRepository feedCategoryRepository;
    
    @Autowired
    private AmenitiesRepository amenitiesRepository;

    @PostMapping(value = "/feed")
    public ResponseEntity<FeedCategoryEntity> addLike(@RequestBody FeedCategoryEntity feedCategoryEntity){
        FeedCategoryEntity response = feedCategoryRepository.save(feedCategoryEntity);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/amenities")
    public ResponseEntity<AmenitiesEntity> addLike(@RequestBody AmenitiesEntity amenitiesEntity){
        AmenitiesEntity response = amenitiesRepository.save(amenitiesEntity);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
