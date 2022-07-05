package com.dooffle.KickOn.data;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="Feed_View_Table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FeedViewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "view_id_sequence")
    @SequenceGenerator(name = "view_id_sequence", sequenceName = "view_id_sequence")
    private long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Long feedId;

}