package com.dooffle.KickOn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto implements Serializable {

    private static String SECRET ="USER_DTO_SECRET";

    private static final long serialVersionUID = 3197855467794374702L;
    private String userId;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String encryptedPassword;
    private Set<RoleDto> roles;
    private String userType;
    private Boolean isVerified;
    private long picId;
    private Boolean isKycDone;
    private Boolean isActive;
    private FileDto pic;
    private String contact;

}
