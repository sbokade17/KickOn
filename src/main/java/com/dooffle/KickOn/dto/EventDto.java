package com.dooffle.KickOn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventDto {

    private long eventId;
    private Set<FileDto> banners;
    private Set<Long> bannerIds;
    private String description;
    private String name;
    private String location;
    private Calendar date;
    private String contact;
    private String registerLink;
    private Set<AmenitiesDto> amenities;
    private Set<Long> amenitiesIds;
    private String capacity;
    private String type;
    private String subType;
}
