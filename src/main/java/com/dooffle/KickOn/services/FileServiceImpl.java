package com.dooffle.KickOn.services;


import com.dooffle.KickOn.data.FileEntity;
import com.dooffle.KickOn.dto.FileDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.repository.FileRepository;
import com.dooffle.KickOn.utils.CommonUtil;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    FileRepository fileRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public FileDto saveToDB(FileDto fileDto) {
        fileDto.setFileByte(CommonUtil.compressBytes(fileDto.getFileByte()));
        FileEntity fileEntity= ObjectMapperUtils.map(fileDto, FileEntity.class);
        fileEntity= fileRepository.save(fileEntity);
        return ObjectMapperUtils.map(fileEntity, FileDto.class);
    }

    @Override
    public FileDto findFileById(Long fileId) {
        try {
            if(fileId==0){
                return null;
            }
            FileEntity fileEntity = fileRepository.findById(fileId).get();
            FileDto fileDto = ObjectMapperUtils.map(fileEntity, FileDto.class);
            fileDto.setFileByte(CommonUtil.decompressBytes(fileDto.getFileByte()));
            return fileDto;
        } catch (RuntimeException e) {
            throw new CustomAppException(HttpStatus.NOT_FOUND, "File with Id " + fileId + " not found!");
        }


    }

    @Override
    public void deleteFileById(Long fileId) {
        try {
            fileRepository.deleteById(fileId);
        } catch (RuntimeException e) {
            throw new CustomAppException(HttpStatus.NOT_FOUND, "File with Id " + fileId + " not found!");
        }
    }

    @Override
    public void updateEventId(Set<Long> fileIds, long id) {
        try {
            String queryString= "update File_table set event_Id=:id where id in ("
                    + fileIds.stream().map(Object::toString).collect(Collectors.joining(","))
                    +")";

            Query query = entityManager.createNativeQuery(
                    queryString
                                );
            query.setParameter("id", id);
            query.executeUpdate();

        } catch (RuntimeException e) {
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Error while updating");
        }
    }

    @Override
    public Set<FileDto> getBannersByEventId(long id) {

        Set<FileEntity> fileEntities = fileRepository.findByEventId(id);
        return new HashSet<>(ObjectMapperUtils.mapAll(fileEntities, FileDto.class));
    }


}
