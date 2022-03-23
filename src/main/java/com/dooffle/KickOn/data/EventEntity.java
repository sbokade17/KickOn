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

    private String description;
    private String name;
    private String location;
    private Calendar date;
    private String contact;
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
    private String capacity;
    private String type;


}
