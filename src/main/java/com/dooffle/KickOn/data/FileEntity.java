package com.dooffle.KickOn.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "File_table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileEntity implements Serializable {
    private static final long serialVersionUID = 3470256101008184650L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_id_sequence")
    @SequenceGenerator(name = "file_id_sequence", sequenceName = "file_id_sequence")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "fileByte", length = 5000)
    private byte[] fileByte;

    @Column
    private String userId;

    @Column
    private Long eventId;


}
