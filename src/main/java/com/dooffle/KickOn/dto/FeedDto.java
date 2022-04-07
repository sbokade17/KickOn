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
public class FeedDto {
    private Long feedId;
    private String title;
    private String link;
    private Calendar date;
    private String content;
    private Integer likes;
    private String keywords;
    private String imageUrl;
    private boolean liked;

}
