package com.lishunyi.minioservice.controller;

import com.lishunyi.minioservice.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MinioController {

    @Autowired
    private MinioService minioService;

    @PostMapping("upload")
    public String uploadFile(@RequestParam("backetName") String backetName, MultipartFile multipartFile) {
        return minioService.uploadFile(backetName, multipartFile);
    }

}
