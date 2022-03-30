package com.dooffle.KickOn.controllers;

import com.dooffle.KickOn.dto.SearchDto;
import com.dooffle.KickOn.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public ResponseEntity<List<SearchDto>> searchAll(@RequestParam(value = "search", required = false) String search){
        List<SearchDto> searchDtos = searchService.findInAll(search);
        return ResponseEntity.status(HttpStatus.OK).body(searchDtos);
    }

    @GetMapping(value = "/{type}/{id}")
    public ResponseEntity<Object> getSearchResult(@PathVariable("type") String type, @PathVariable("id") Long id){
        Object result = searchService.getResultDetail(type, id);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
