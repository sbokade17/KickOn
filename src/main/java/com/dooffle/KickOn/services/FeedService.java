package com.dooffle.KickOn.services;

import com.dooffle.KickOn.dto.FeedDto;

import java.util.List;

public interface FeedService {
    List<FeedDto> addAndGetFeeds(List<FeedDto> feedDtos, String search);

    void addLike(Long id);

    FeedDto getFeedById(Long id);

    void removeLike(Long id);
}
