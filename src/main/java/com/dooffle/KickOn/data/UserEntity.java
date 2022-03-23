package com.dooffle.KickOn.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Users_Table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserEntity implements Serializable {

    private static final long serialVersionUID = -524477455009949841L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_sequence")
    @SequenceGenerator(name = "user_id_sequence", sequenceName = "user_id_sequence")
    private long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(length = 120, unique = true)
    private String email;

    @Column(unique = true)
    private String encryptedPassword;

    @Column
    private Boolean isVerified = false;

    @Column
    private long picId;

    @Column
    private Boolean isKycDone = false;

    @Column
    private Boolean isActive = false;

    @Column
    private String contact;

    @Column
    private Calendar createdOn;



    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "USER_ROLES",
            joinColumns = { @JoinColumn(name = "USER_ID")},
            inverseJoinColumns = { @JoinColumn(name = "ROLE_ID")})
    private Set<RoleEntity> roles= new HashSet<>();


}
