package com.dooffle.KickOn.repository;

import com.dooffle.KickOn.data.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    UserEntity findByUserId(String userId);

    UserEntity findByEmail(String email);

    List<UserEntity> findByIsVerifiedAndIsKycDone(boolean isVerified, boolean isKycDone);

    UserEntity findByEmailOrContact(String email, String contact);
}
