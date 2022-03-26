package com.dooffle.KickOn.services;

import com.dooffle.KickOn.dto.PasswordUpdateDto;
import com.dooffle.KickOn.dto.StatusDto;
import com.dooffle.KickOn.dto.UserDto;

import java.util.Map;

public interface UserAccountService {
    UserDto resetPassword(PasswordUpdateDto passwordUpdateDto);

    String updatePic(Long picId);

    UserDto patchAccount(Map<String, Object> patchObject);

    StatusDto updatePassword(PasswordUpdateDto passwordUpdateDto);
}