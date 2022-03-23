package com.dooffle.KickOn.repository;


import com.dooffle.KickOn.data.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    Set<FileEntity> findByEventId(long id);
}
