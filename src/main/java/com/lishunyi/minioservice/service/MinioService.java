package com.lishunyi.minioservice.service;

import com.lishunyi.minioservice.model.UploadDTO;
import org.springframework.web.multipart.MultipartFile;

public interface MinioService {

    /**
     * 文件上传
     *
     * @param bucketName    存储桶
     * @param multipartFile 文件流
     * @return
     */
    String uploadFile(String bucketName, MultipartFile multipartFile, UploadDTO uploadDTO);
}
