package com.dooffle.KickOn.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="Like_Table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "like_id_sequence")
    @SequenceGenerator(name = "like_id_sequence", sequenceName = "like_id_sequence")
    private long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Long feedId;

    @Column(nullable = false)
    private String type;
}
