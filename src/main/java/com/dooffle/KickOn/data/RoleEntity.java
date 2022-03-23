package com.dooffle.KickOn.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Role_Table")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleEntity implements Serializable {

    private static final long serialVersionUID = -888719313306799984L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_sequence")
    @SequenceGenerator(name = "role_id_sequence", sequenceName = "role_id_sequence")
    private long id;

    @Column(nullable = false)
    @NaturalId
    private String name;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "roles")
    private Set<UserEntity> users = new HashSet<>();


}
