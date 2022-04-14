package com.dooffle.KickOn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchDto {
    private String name;
    private String type;
    private String id;
    private String image;
    private Calendar date;
    private String link;
}
