package com.dooffle.KickOn.services;

import com.dooffle.KickOn.dto.LocationDto;

import java.util.List;
import java.util.Set;

public interface LocationService {
    Set<LocationDto> getLocations();

    LocationDto addLocation(LocationDto location);

    void deleteById(Long locId);

    LocationDto findById(Long locationId);

    List<LocationDto> addLocations(List<LocationDto> locationDtos);
}
