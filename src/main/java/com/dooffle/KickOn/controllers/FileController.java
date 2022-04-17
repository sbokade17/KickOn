package com.dooffle.KickOn.controllers;


import com.dooffle.KickOn.dto.FileDto;
import com.dooffle.KickOn.dto.StatusDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.services.FileService;
import com.dooffle.KickOn.utils.CommonUtil;
import com.dooffle.KickOn.utils.Constants;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    FileService fileService;

    @PostMapping("/upload")
    public FileDto uploadImage(@RequestParam("file")MultipartFile multipartFile) {
        try {
            FileDto fileDto = new FileDto();
            fileDto.setName(multipartFile.getOriginalFilename());
            fileDto.setType(multipartFile.getContentType());
            fileDto.setUserId(CommonUtil.getLoggedInUserId());
            fileDto.setFileByte(multipartFile.getBytes());
            FileDto file = fileService.saveToDB(fileDto);
            return ObjectMapperUtils.map(file, FileDto.class);
        } catch (Exception e) {
           throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY,e.getMessage());
        }

    }

    @GetMapping(value = "/{fileId}", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> getFileById(@PathVariable("fileId") Long fileId) throws IOException{
        try{
            FileDto fileDto = fileService.findFileById(fileId);
            byte[] result = fileDto.getFileByte();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDto.getName() + "\"")
                    .body(result);

        }catch (RuntimeException e){
            throw new CustomAppException(HttpStatus.NOT_FOUND, e.getMessage());
        }


    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<StatusDto> deleteFileById(@PathVariable("fileId") Long fileId) throws IOException{
        try{

            fileService.deleteFileById(fileId);
            return ResponseEntity.status(HttpStatus.OK).body(new StatusDto(Constants.DELETED));
        }catch (RuntimeException e){
            throw new CustomAppException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }


}
