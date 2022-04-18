package com.dooffle.KickOn.controllers;

import com.dooffle.KickOn.dto.LocationDto;
import com.dooffle.KickOn.dto.StatusDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.services.LocationService;
import com.dooffle.KickOn.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    LocationService locationService;

    @GetMapping
    public ResponseEntity<Set<LocationDto>> getLocations(){
        Set<LocationDto> eventDtos = locationService.getLocations();
        return ResponseEntity.status(HttpStatus.OK).body(eventDtos);
    }

    @PostMapping
    public ResponseEntity<LocationDto> addLocation(@RequestBody LocationDto location){
        LocationDto locationDto = locationService.addLocation(location);
        return ResponseEntity.status(HttpStatus.OK).body(locationDto);
    }

    @PostMapping(value = "all")
    public ResponseEntity<List<LocationDto>> addLocation(@RequestBody List<String> locations){
        List<LocationDto> locationDtos = new ArrayList<>();
        locations.forEach(x->{
            LocationDto dto = new LocationDto();
            dto.setLatitude(0.0);
            dto.setLongitude(0.0);
            dto.setLocName(x);
            locationDtos.add(dto);
        });
        List<LocationDto> locationDto = locationService.addLocations(locationDtos);
        return ResponseEntity.status(HttpStatus.OK).body(locationDto);
    }

    @DeleteMapping("/{locId}")
    public ResponseEntity<StatusDto> deleteFileById(@PathVariable("locId") Long locId) throws IOException {
        try{

            locationService.deleteById(locId);
            return ResponseEntity.status(HttpStatus.OK).body(new StatusDto(Constants.DELETED));
        }catch (RuntimeException e){
            throw new CustomAppException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }


}
