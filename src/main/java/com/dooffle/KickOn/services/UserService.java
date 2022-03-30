package com.dooffle.KickOn.services;

import com.dooffle.KickOn.dto.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);

    UserDto getUserDetailsByEmail(String email);

    void sendOtpOnMail(String userInput);

    void sendOtpOnMobile(String userInput) throws NoSuchMethodException;

    UserDto validateOtp(OtpDto otpDto);

    UserDto resetPassword(PasswordUpdateDto userDto);

    void sendNewPasswordMail(String email);

    Set<RoleDto> getExistingRole(String userType);

    UserDto getUserDetails(String id);

    String updatePic(Long picId);

    UserDto patchUser(Map<String, Object> patchObject);

    StatusDto updatePassword(PasswordUpdateDto passwordUpdateDto);

    void setUserKycDone();

    List<UserDto> getPendingVendors();

    void validateMobileOtp(String mobile, String otp);
}
