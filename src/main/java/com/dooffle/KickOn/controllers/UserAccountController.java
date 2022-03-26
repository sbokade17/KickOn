package com.dooffle.KickOn.controllers;


import com.dooffle.KickOn.dto.PasswordUpdateDto;
import com.dooffle.KickOn.dto.StatusDto;
import com.dooffle.KickOn.dto.UserDetailsDto;
import com.dooffle.KickOn.dto.UserDto;
import com.dooffle.KickOn.models.PasswordResetRequestModel;
import com.dooffle.KickOn.services.UserAccountService;
import com.dooffle.KickOn.utils.Constants;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/account")
public class UserAccountController {

    @Autowired
    UserAccountService userAccountService;

    @PostMapping(value = "/updatePic")
    public ResponseEntity<String> updatePic(@RequestBody Long picId){
        String status = userAccountService.updatePic(picId);
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }

    @PatchMapping
    public ResponseEntity<UserDetailsDto> patchAccount(@RequestBody Map<String,Object> patchObject){

        UserDto userDto= userAccountService.patchAccount(patchObject);
        return ResponseEntity.status(HttpStatus.OK).body(ObjectMapperUtils.map(userDto, UserDetailsDto.class));
    }

    @PostMapping(value = "/updatePassword")
    public ResponseEntity<StatusDto> updatePassword(@Valid @RequestBody PasswordResetRequestModel passwordResetRequestModel){
        try {
            PasswordUpdateDto passwordUpdateDto= ObjectMapperUtils.map(passwordResetRequestModel, PasswordUpdateDto.class);
            StatusDto statusDto = userAccountService.updatePassword(passwordUpdateDto);
            return ResponseEntity.status(HttpStatus.OK).body(statusDto);
        }catch (RuntimeException re){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusDto(Constants.FAILED));
        }

    }


}