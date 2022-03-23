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
public class OtpDto implements Serializable {

    private static final long serialVersionUID = 8244564815769724412L;
    private String email;
    private String mobile;
    private String otp;

}
