package com.dooffle.KickOn.services;

import com.dooffle.KickOn.dto.FileDto;

import java.util.List;
import java.util.Set;

public interface FileService {
    FileDto saveToDB(FileDto fileDto);

    FileDto findFileById(Long fileId);

    void deleteFileById(Long fileId);

    void updateEventId(Set<Long> collect, long id);

    Set<FileDto> getBannersByEventId(long id);
}
