package com.dooffle.KickOn.services;

import com.dooffle.KickOn.dto.PasswordUpdateDto;
import com.dooffle.KickOn.dto.StatusDto;
import com.dooffle.KickOn.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    UserService userService;

    @Override
    public UserDto resetPassword(PasswordUpdateDto passwordUpdateDto) {
        return userService.resetPassword(passwordUpdateDto);

    }

    @Override
    public String updatePic(Long picId) {
        return userService.updatePic(picId);
    }

    @Override
    public UserDto patchAccount(Map<String, Object> patchObject) {

        return userService.patchUser(patchObject);

    }

    @Override
    public StatusDto updatePassword(PasswordUpdateDto passwordUpdateDto) {
        return userService.updatePassword(passwordUpdateDto);
    }
}