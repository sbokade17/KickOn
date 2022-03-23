package com.dooffle.KickOn.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Amenities_Table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AmenitiesEntity implements Serializable {

    private static final long serialVersionUID = 2843667505301264495L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "amenity_id_sequence")
    @SequenceGenerator(name = "amenity_id_sequence", sequenceName = "amenity_id_sequence")
    private long id;
    
    @Column
    private String name;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "amenities")
    private Set<EventEntity> amenities = new HashSet<>();
}
