package com.dooffle.KickOn.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="Feed_Category_Mapping")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FeedCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fc_id_sequence")
    @SequenceGenerator(name = "fc_id_sequence", sequenceName = "fc_id_sequence")
    private Long id;

    @Column
    private String categoryName;

    @Column
    private String categoryUrl;

    @Column
    private Long displayOrder;
}
