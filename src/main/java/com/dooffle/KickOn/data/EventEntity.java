package com.dooffle.KickOn.data;

import com.dooffle.KickOn.dto.FileDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="Event_Table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventEntity implements Serializable {

    private static final long serialVersionUID = 3811989948924740793L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_id_sequence")
    @SequenceGenerator(name = "event_id_sequence", sequenceName = "event_id_sequence")
    private long eventId;

    @Column
    private String description;
    @Column
    private String name;
    @Column
    private String location;
    @Column
    private Calendar date;
    @Column
    private String contact;
    @Column
    private String registerLink;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "EVENT_AMENITIES",
            joinColumns = { @JoinColumn(name = "eventId")},
            inverseJoinColumns = { @JoinColumn(name = "id")})
    private Set<AmenitiesEntity> amenities;
    @Column
    private String capacity;
    @Column
    private String type;
    @Column
    private String subType;
    @Column
    private Integer likes = 0;


}
