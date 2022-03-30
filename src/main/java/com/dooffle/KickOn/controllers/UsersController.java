package com.dooffle.KickOn.controllers;


import com.dooffle.KickOn.dto.OtpDto;
import com.dooffle.KickOn.dto.PasswordUpdateDto;
import com.dooffle.KickOn.dto.StatusDto;
import com.dooffle.KickOn.dto.UserDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.models.CreateUserDetailsModel;
import com.dooffle.KickOn.models.CreateUserResponseModel;
import com.dooffle.KickOn.models.GetUserDetailsResponseModel;
import com.dooffle.KickOn.models.ValidateOtpRequestModel;
import com.dooffle.KickOn.services.DeviceService;
import com.dooffle.KickOn.services.UserService;
import com.dooffle.KickOn.utils.CommonUtil;
import com.dooffle.KickOn.utils.Constants;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {


    @Autowired
    Environment environment;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private DeviceService deviceService;



    @GetMapping(value = "/info")
    public ResponseEntity<GetUserDetailsResponseModel> getUserName() {

        UserDto user = userService.getUserDetails(CommonUtil.getLoggedInUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ObjectMapperUtils.map(user, GetUserDetailsResponseModel.class));
    }


    @PostMapping(value = "device")
    public ResponseEntity<String> updateDevice(@RequestBody String deviceId) {

        try{
            deviceService.saveDevice(CommonUtil.getLoggedInUserId(), deviceId);
            return ResponseEntity.status(HttpStatus.OK).body("Success");
        }catch (RuntimeException re){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Error");
        }

    }

    @DeleteMapping(value = "/device/{deviceId}")
    public ResponseEntity<String> deleteDevice(@RequestBody String deviceId) {

        try{
            deviceService.deleteDevice(deviceId);
            return ResponseEntity.status(HttpStatus.OK).body("Success");
        }catch (RuntimeException re){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Error");
        }

    }

    @PostMapping
    public ResponseEntity<CreateUserResponseModel> createUser(@Valid @RequestBody CreateUserDetailsModel userDetails) {

        UserDto user = userService.createUser(ObjectMapperUtils.map(userDetails, UserDto.class));
        return ResponseEntity.status(HttpStatus.CREATED).body(ObjectMapperUtils.map(user, CreateUserResponseModel.class));
    }

    @GetMapping(value = "/{emailId}/otp")
    public ResponseEntity<StatusDto> getOtp(@PathVariable("emailId") String userInput) throws NoSuchMethodException {

        if (userInput == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusDto(Constants.FAILED));
        }
        if (userInput.contains("@")) {
            userService.sendOtpOnMail(userInput);
        } else {
            userService.sendOtpOnMobile(userInput);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new StatusDto(Constants.SUCCESS));
    }

    @GetMapping(value = "/{mobile}/otp/{otp}")
    public ResponseEntity<StatusDto> validateOtp(@PathVariable("mobile") String mobile, @PathVariable("otp") String otp) throws NoSuchMethodException {

        if (mobile == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusDto(Constants.FAILED));
        }

            userService.validateMobileOtp(mobile, otp);


        return ResponseEntity.status(HttpStatus.CREATED).body(new StatusDto(Constants.SUCCESS));
    }

    @PostMapping(value = "/validateOtp/reset")
    public ResponseEntity<CreateUserResponseModel> validateOtpVerifyAccount(@Valid @RequestBody ValidateOtpRequestModel validateOtpRequestModel) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OtpDto otpDto = modelMapper.map(validateOtpRequestModel, OtpDto.class);
        UserDto userDto = userService.validateOtp(otpDto);
        if(userDto!=null){
            PasswordUpdateDto passwordUpdateDto= new PasswordUpdateDto();
            passwordUpdateDto.setUserId(userDto.getUserId());
            passwordUpdateDto.setPassword(validateOtpRequestModel.getPassword());
            userService.resetPassword(passwordUpdateDto);
        }else {
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Unable to validate OTP!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.map(userDto, CreateUserResponseModel.class));
    }

    @PostMapping(value = "/resetPassword")
    public ResponseEntity<StatusDto> resetPassword(@Valid @RequestBody PasswordUpdateDto passwordUpdateDto) {

        passwordUpdateDto.setUserId(CommonUtil.getLoggedInUserId());
        UserDto user = userService.resetPassword(passwordUpdateDto);
        if (user != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new StatusDto("Password reset successful"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusDto("Error while fetching user details!"));
    }

}
