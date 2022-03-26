package com.dooffle.KickOn.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "Location_Table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loc_id_sequence")
    @SequenceGenerator(name = "loc_id_sequence", sequenceName = "loc_id_sequence")
    private Long locId;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(unique = true)
    private String locName;
}
