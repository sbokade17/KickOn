package com.dooffle.KickOn.repository;


import com.dooffle.KickOn.data.RoleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

    Set<RoleEntity> findByName(String name);
}
