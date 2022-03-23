package com.dooffle.KickOn.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@Table(name="Otp_Table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OtpEntity implements Serializable {
    private static final long serialVersionUID = 2710476707465807149L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "otp_id_sequence")
    @SequenceGenerator(name = "otp_id_sequence", sequenceName = "otp_id_sequence")
    private long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private Calendar validity;


}
