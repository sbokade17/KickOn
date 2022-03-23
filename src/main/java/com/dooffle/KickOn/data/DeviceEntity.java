package com.dooffle.KickOn.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="Device_Table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeviceEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_id_sequence")
    @SequenceGenerator(name = "device_id_sequence", sequenceName = "device_id_sequence")
    private long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String deviceId;


}
