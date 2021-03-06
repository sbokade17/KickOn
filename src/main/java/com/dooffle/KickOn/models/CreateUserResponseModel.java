package com.dooffle.KickOn.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateUserResponseModel {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String contact;
    private String locationId;

}
