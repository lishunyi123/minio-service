package com.lishunyi.minioservice.controller;

import com.lishunyi.minioservice.model.UploadDTO;
import com.lishunyi.minioservice.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MinioController {

    @Autowired
    private MinioService minioService;

    @PostMapping("upload/{bucket-name}")
    public String uploadFile(@PathVariable(value = "bucket-name") String bucketName, UploadDTO uploadDTO) {
        return minioService.uploadFile(bucketName, uploadDTO.getMultipartFile(), uploadDTO);
    }

    @DeleteMapping("delete/{bucket-name}")
    public void deleteFile(@PathVariable(value = "bucket-name") String bucketName, @RequestParam("url") String url) {
        minioService.deleteFile(bucketName, url);
    }

    @DeleteMapping("delete")
    public void deleteFileByUrl(@RequestParam("url") String url) {
        minioService.deleteFile(url);
    }
}
