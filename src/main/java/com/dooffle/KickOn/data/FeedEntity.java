package com.dooffle.KickOn.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name="Feed_Table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FeedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feed_id_sequence")
    @SequenceGenerator(name = "feed_id_sequence", sequenceName = "feed_id_sequence")
    private Long feedId;

    @Column(length = 1024)
    private String title;
    @Column(length = 1024)
    private String link;
    @Column
    private Calendar date;
    @Column(columnDefinition="TEXT")
    private String content;
    @Column
    private Integer likes = 0;
    @Column(length = 1024)
    private String keywords;
    @Column(length = 1024)
    private String imageUrl;
    
}
