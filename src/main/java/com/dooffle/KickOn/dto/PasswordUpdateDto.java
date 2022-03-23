package com.dooffle.KickOn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PasswordUpdateDto implements Serializable {

    private static final long serialVersionUID = 3728736151828955521L;

    private String userId;
    private String password;
    private String oldPassword;


}
