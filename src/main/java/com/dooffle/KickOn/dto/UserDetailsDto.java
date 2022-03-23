package com.dooffle.KickOn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDetailsDto {

    private String userId;
    private String firstName;
    private String lastName;
    private FileDto pic;
    private long picId;
    private String contact;
    private String email;
}
