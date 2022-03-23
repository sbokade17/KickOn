package com.dooffle.KickOn.models;

import com.dooffle.KickOn.data.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetUserDetailsResponseModel {
    private String userId;
    private String firstName;
    private String lastName;
    private Set<RoleEntity> roles;
    private String email;
    private long picId;
    private String contact;
    private String token;
    private String userType;

}