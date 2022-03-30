package com.dooffle.KickOn.services;

import com.dooffle.KickOn.dto.SearchDto;

import java.util.List;
import java.util.Map;

public interface SearchService {
    List<SearchDto> findInAll(String search);

    Object getResultDetail(String type, Long id);
}
