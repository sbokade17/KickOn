package com.dooffle.KickOn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LocationDto implements Comparable<LocationDto>{
    private Long locId;
    private Double latitude;
    private Double longitude;
    private String locName;

    @Override
    public int compareTo(LocationDto other) {
        return String.CASE_INSENSITIVE_ORDER.compare(getLocName(), other.getLocName());
    }
}
