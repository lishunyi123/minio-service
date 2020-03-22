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

    /**
     * 删除oss文件
     *
     * @param bucketName
     * @param objectName
     */
    void deleteFile(String bucketName, String objectName);

    /**
     * 根据链接删除文件
     *
     * @param url
     */
    void deleteFile(String url);
}
