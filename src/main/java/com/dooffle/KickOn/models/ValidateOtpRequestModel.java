package com.dooffle.KickOn.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ValidateOtpRequestModel {
    @NotNull(message = "Email cannot be null")
    @Email
    private String email;

    @NotNull(message = "Mobile cannot be null")
    private String mobile;

    @NotNull(message = "OTP cannot be null")
    private String otp;

    @NotNull(message = "Password cannot be null")
    private String password;

}
