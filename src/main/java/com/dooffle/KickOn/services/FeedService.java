package com.dooffle.KickOn.services;

import com.dooffle.KickOn.dto.CategoriesDto;
import com.dooffle.KickOn.dto.FeedDto;

import java.util.List;

public interface FeedService {
    List<FeedDto> addAndGetFeeds( String keyword, int start, int end, String category);

    void addLike(Long id);

    FeedDto getFeedById(Long id);

    void removeLike(Long id);

    String getFeedsUrl(String category);

    List<CategoriesDto> getAllCategories();

    List<FeedDto> getFeedsByCategoryIn(String category, int start, int end);

}
