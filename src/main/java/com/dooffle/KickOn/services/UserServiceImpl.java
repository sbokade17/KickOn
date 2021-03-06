package com.dooffle.KickOn.services;


import com.dooffle.KickOn.data.OtpEntity;
import com.dooffle.KickOn.data.RoleEntity;
import com.dooffle.KickOn.data.UserEntity;
import com.dooffle.KickOn.dto.*;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.fcm.service.PushNotificationService;
import com.dooffle.KickOn.repository.OtpRepository;
import com.dooffle.KickOn.repository.RoleRepository;
import com.dooffle.KickOn.repository.UserRepository;
import com.dooffle.KickOn.utils.CommonUtil;
import com.dooffle.KickOn.utils.Constants;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;


@Service
public class UserServiceImpl implements UserService {

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    EmailService emailService;

    @Autowired
    FileService fileService;

    @Autowired
    LocationService locationService;

    @Autowired
    PushNotificationService notificationService;

    @Override
    public UserDto createUser(UserDto userDto) {

        userDto.setUserId(UUID.randomUUID().toString());
        userDto.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

        UserEntity userEntity = ObjectMapperUtils.map(userDto, UserEntity.class);
        try {
            userEntity.setCreatedOn(Calendar.getInstance());
            userRepository.save(userEntity);
            Set<RoleEntity> roleEntities = roleRepository.findByName(userDto.getUserType());
            if (roleEntities.size() == 0) {
                RoleEntity roleEntity = new RoleEntity();
                roleEntity.setName(userDto.getUserType());
                RoleEntity savedRoleEntity = roleRepository.save(roleEntity);
                roleEntities.add(savedRoleEntity);
            }
            userEntity.setRoles(roleEntities);
            if(Constants.USER.equals(userDto.getUserType())){
                userEntity.setIsActive(true);
            }
            userEntity = userRepository.save(userEntity);

          //  emailService.sendRegisterMail(userEntity.getEmail(), userEntity.getFirstName());
        } catch (UnexpectedRollbackException ex) {
            if (ex.getMostSpecificCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMostSpecificCause().getMessage());
            }
        } catch (DataIntegrityViolationException psqlException ) {

            if(psqlException.getMostSpecificCause().getMessage().contains("(email)=(")){
                throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Email already exist!");
            }
            if(psqlException.getMostSpecificCause().getMessage().contains("(contact)=(")){
                throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Contact already exist!");
            }
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, psqlException.getMostSpecificCause().getMessage());

        }
        return ObjectMapperUtils.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity user = userRepository.findByEmailOrContact(email,email);
        if(user == null) throw new UsernameNotFoundException(email);
        return ObjectMapperUtils.map(user,UserDto.class);
    }

    @Override
    public UserDto getUserDetailsByEmailWithoutException(String email) {
        UserEntity user = userRepository.findByEmailOrContact(email,email);
        if(user == null) return null;
        return ObjectMapperUtils.map(user,UserDto.class);
    }

    @Override
    @Transactional
    public void sendOtpOnMail(String userInput, boolean reset) {
        //UserDto userDto=getUserDetailsByEmail(userInput);

        UserDto userDto=getUserDetailsByEmailWithoutException(userInput);
        if(userDto==null && reset){
            throw new CustomAppException(HttpStatus.BAD_REQUEST, "User is not registered.");
        }
        if(userDto!=null && !reset){
            throw new CustomAppException(HttpStatus.BAD_REQUEST, "User already registered.");
        }

        OtpEntity otpEntity=otpRepository.findByEmail(userInput);

        if(null!=otpEntity && otpEntity.getValidity().before(Calendar.getInstance())){
            otpRepository.deleteById(otpEntity.getId());
            otpEntity=null;
        }

        if(null==otpEntity){
            String otp=CommonUtil.getRandomNumberString();
            otpEntity= new OtpEntity();
            otpEntity.setOtp(otp);
            otpEntity.setMobile("NA");
            otpEntity.setEmail(userInput);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 5);
            otpEntity.setValidity(calendar); //5 minutes validity
            otpRepository.save(otpEntity);
            emailService.sendResetMail(userInput,otp, "User");

        }else{
            throw new CustomAppException(HttpStatus.BAD_REQUEST, "OTP already sent to User.");
        }



    }

    @Override
    @Transactional
    public void sendOtpOnMobile(String userInput, boolean reset) throws NoSuchMethodException {
        UserDto userDto=getUserDetailsByEmailWithoutException(userInput);
        if(userDto==null && reset){
            throw new CustomAppException(HttpStatus.BAD_REQUEST, "User is not registered.");
        }
        if(userDto!=null && !reset){
            throw new CustomAppException(HttpStatus.BAD_REQUEST, "User already registered.");
        }

        OtpEntity otpEntity=otpRepository.findByMobile(userInput);

        if(null!=otpEntity && otpEntity.getValidity().before(Calendar.getInstance())){
            otpRepository.deleteById(otpEntity.getId());
            otpEntity=null;
        }

        if(null==otpEntity){
            String otp=CommonUtil.getRandomNumberString();
            otpEntity = new OtpEntity();
            otpEntity.setOtp(otp);
            otpEntity.setMobile(userInput);
            otpEntity.setEmail("NA");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 5);
            otpEntity.setValidity(calendar); //5 minutes validity
            otpRepository.save(otpEntity);
            final String uri = "http://smslogin.pcexpert.in/api/mt/SendSMS?DCS=0&flashsms=0&user=kickon&password=846342&senderid=KICKON&channel=Trans&number="+userInput+"&text=Welcome User, Use "+otp+" to kick start the World of Football. Please do not share this OTP. Team KickOn Football&route=67";

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(uri, Map.class);
        }else{
            throw new CustomAppException(HttpStatus.BAD_REQUEST, "OTP already sent to User.");
        }

    }

    @Override
    @Transactional
    public UserDto validateOtp(OtpDto otpDto) {
        UserDto userDto = null;
        OtpEntity otpEntity= null;
        if(null!=otpDto){
            otpEntity=otpRepository.findByEmail(otpDto.getEmail());
        }else {
            otpEntity=otpRepository.findByMobile(otpDto.getMobile());
        }

        if(null!=otpEntity){
            if( otpEntity.getValidity().before(new Date(System.currentTimeMillis()))){
                throw new CustomAppException(HttpStatus.BAD_REQUEST, "OTP has expired");
            }
            if(!otpDto.getOtp().equals(otpEntity.getOtp())){
                throw new CustomAppException(HttpStatus.BAD_REQUEST, "Incorrect OTP");
            }
            otpRepository.deleteById(otpEntity.getId());
            userDto=getUserDetailsByEmail(otpDto.getEmail());
        }
        return userDto;
    }

    @Override
    public UserDto resetPassword(PasswordUpdateDto passwordUpdateDto) {
        try{
            UserEntity user = userRepository.findByEmailOrContact(passwordUpdateDto.getEmail(),passwordUpdateDto.getEmail());

//            if(passwordUpdateDto.getOldPassword()!=null){
//                if(!bCryptPasswordEncoder.matches(passwordUpdateDto.getOldPassword(),user.getEncryptedPassword())){
//                    throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Old password does not match!");
//
//                }
//            }
            user.setEncryptedPassword(bCryptPasswordEncoder.encode(passwordUpdateDto.getPassword()));
            user = userRepository.save(user);
            return ObjectMapperUtils.map(user, UserDto.class);
        }catch (CustomAppException pe){
            throw pe;
        }
        catch (RuntimeException re){
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Error while updating password!");
        }


    }

    @Override
    public void sendNewPasswordMail(String email) {
        UserEntity user = userRepository.findByEmail(email);
        String sysGenPass=CommonUtil.generateRandomPassword();
        user.setEncryptedPassword(bCryptPasswordEncoder.encode(sysGenPass));
        userRepository.save(user);
       // emailService.sendMail(email,"New Password for PetApp","New Password for PetApp login is: "+sysGenPass);

    }

    @Override
    public Set<RoleDto> getExistingRole(String userType) {
        return new HashSet<RoleDto>(ObjectMapperUtils.mapAll(roleRepository.findByName(userType),RoleDto.class));
    }

    @Override
    public UserDto getUserDetails(String id) {
        UserEntity user = userRepository.findByUserId(id);
        if (user == null) throw new CustomAppException(HttpStatus.NOT_FOUND, "User with userId " + id + " not found!");
        UserDto userDto =ObjectMapperUtils.map(user, UserDto.class);
        if(user.getPicId()!=0){
            userDto.setPic(fileService.findFileById(user.getPicId()));
        }
        return userDto;
    }

    @Override
    public String updatePic(Long picId) {
        try{
            UserEntity user = userRepository.findByUserId(CommonUtil.getLoggedInUserId());
            user.setPicId(picId);
            userRepository.save(user);
            return "Success";
        }catch (RuntimeException re){
            throw new CustomAppException(HttpStatus.BAD_REQUEST, "Error while saving pic");
        }

    }

    @Override
    public UserDto patchUser(Map<String, Object> patchObject) {
        try{
            UserEntity user = userRepository.findByUserId(CommonUtil.getLoggedInUserId());
            patchObject.remove("id");
            patchObject.remove("userId");
            patchObject.remove("encryptedPassword");
            patchObject.remove("roles");
            user = ObjectMapperUtils.map(patchObject, user);
            userRepository.save(user);
            return ObjectMapperUtils.map(user, UserDto.class);
        } catch (DataIntegrityViolationException psqlException ) {

            if(psqlException.getMostSpecificCause().getMessage().contains("(email)=(")){
                throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Email already exist!");
            }
            if(psqlException.getMostSpecificCause().getMessage().contains("(contact)=(")){
                throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Contact already exist!");
            }
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, psqlException.getMostSpecificCause().getMessage());

        }catch (RuntimeException re){
            throw new CustomAppException(HttpStatus.BAD_REQUEST, "Error while saving user");
        }
    }

    @Override
    public StatusDto updatePassword(PasswordUpdateDto passwordUpdateDto) {
        UserEntity userEntity = userRepository.findByUserId(CommonUtil.getLoggedInUserId());
        passwordUpdateDto.setEmail(userEntity.getEmail());
        resetPassword(passwordUpdateDto);
        return new StatusDto(Constants.SUCCESS);
    }

    @Override
    public void setUserKycDone() {
        UserEntity userEntity = userRepository.findByUserId(CommonUtil.getLoggedInUserId());
        userEntity.setIsKycDone(true);
        userRepository.save(userEntity);
    }

    @Override
    public List<UserDto> getPendingVendors() {
        List<UserEntity> userEntities = userRepository.findByIsVerifiedAndIsKycDone(false,true);
        return ObjectMapperUtils.mapAll(userEntities, UserDto.class);
    }


    @Override
    public void validateMobileOtp(String mobile, String otp) {
        OtpEntity otpEntity= otpRepository.findByMobile(mobile);


        if(null!=otpEntity){
            if( otpEntity.getValidity().before(new Date(System.currentTimeMillis()))){
                throw new CustomAppException(HttpStatus.BAD_REQUEST, "OTP has expired");
            }
            if(!otp.equals(otpEntity.getOtp())){
                throw new CustomAppException(HttpStatus.BAD_REQUEST, "Incorrect OTP");
            }
            otpRepository.deleteById(otpEntity.getId());

        }

    }

    @Override
    @Transactional
    public LocationDto saveLocation(Long locationId) {
        LocationDto locationDto = locationService.findById(locationId);
        UserEntity userEntity = userRepository.findByUserId(CommonUtil.getLoggedInUserId());
        userEntity.setLocationId(locationId.toString());
        userRepository.save(userEntity);
        notificationService.subscribeToTopic(locationDto.getLocName());
        return locationDto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmailOrContact(username, username);
        if(user == null) throw new UsernameNotFoundException(username);

        return new User(user.getEmail()!=null?user.getEmail():user.getContact(),user.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
    }



}
