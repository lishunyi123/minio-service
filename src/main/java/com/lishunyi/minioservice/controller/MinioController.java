package com.lishunyi.minioservice.controller;

import com.lishunyi.minioservice.model.UploadDTO;
import com.lishunyi.minioservice.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MinioController {

    @Autowired
    private MinioService minioService;

    @PostMapping("upload/{bucket-name}")
    public String uploadFile(@PathVariable(value = "bucket-name") String bucketName, @RequestParam("file") MultipartFile multipartFile, UploadDTO uploadDTO) {
        return minioService.uploadFile(bucketName, multipartFile, uploadDTO);
    }

}
