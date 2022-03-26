package com.dooffle.KickOn.services;

import com.dooffle.KickOn.data.LocationEntity;
import com.dooffle.KickOn.dto.LocationDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.repository.LocationRepository;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LocationServiceImpl implements LocationService{

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public Set<LocationDto> getLocations() {
        List<LocationEntity> locationEntities = locationRepository.findAll();
        return new HashSet<>(ObjectMapperUtils.mapAll(locationEntities, LocationDto.class));
    }

    @Override
    public LocationDto addLocation(LocationDto location) {
        LocationEntity locationEntity = ObjectMapperUtils.map(location, LocationEntity.class);
        locationEntity = locationRepository.save(locationEntity);
        return ObjectMapperUtils.map(locationEntity, LocationDto.class);
    }

    @Override
    public void deleteById(Long locId) {
        try {
            locationRepository.deleteById(locId);
        } catch (RuntimeException e) {
            throw new CustomAppException(HttpStatus.NOT_FOUND, "Location with Id " + locId + " not found!");
        }
    }
}
