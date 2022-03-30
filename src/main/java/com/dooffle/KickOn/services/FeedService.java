package com.dooffle.KickOn.services;

import com.dooffle.KickOn.dto.FeedDto;

import java.util.List;

public interface FeedService {
    List<FeedDto> addAndGetFeeds(List<FeedDto> feedDtos);

    void addLike(Long id);

    FeedDto getFeedById(Long id);
}
