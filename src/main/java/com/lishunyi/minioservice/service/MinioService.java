package com.lishunyi.minioservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {

    /**
     * 文件上传
     *
     * @param backetName    存储桶
     * @param multipartFile 文件流
     * @return
     */
    String uploadFile(String backetName, MultipartFile multipartFile);
}
