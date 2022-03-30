package com.dooffle.KickOn.repository;


import com.dooffle.KickOn.data.OtpEntity;
import org.springframework.data.repository.CrudRepository;

public interface OtpRepository extends CrudRepository<OtpEntity, Long> {

    OtpEntity findByEmail(String email);

    OtpEntity findByMobile(String mobile);
}
