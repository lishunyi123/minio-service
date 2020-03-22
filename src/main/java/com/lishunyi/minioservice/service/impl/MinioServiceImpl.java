package com.lishunyi.minioservice.service.impl;

import com.lishunyi.minioservice.config.OSSClient;
import com.lishunyi.minioservice.model.UploadDTO;
import com.lishunyi.minioservice.service.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class MinioServiceImpl implements MinioService {

    @Autowired
    private OSSClient ossClient;

    /**
     * 文件上传
     *
     * @param bucketName    存储桶
     * @param multipartFile 文件流
     * @return
     */
    @Override
    public String uploadFile(String bucketName, MultipartFile multipartFile, UploadDTO uploadDTO) {
        // 存储桶不存在抛出异常
        ossClient.doesBucketExistError(bucketName);
        String fileName = ossClient.putObject(bucketName, multipartFile, uploadDTO);
        return ossClient.getObjectUrl(bucketName, fileName);
    }

    /**
     * 删除oss文件
     *
     * @param bucketName
     * @param objectName
     */
    @Override
    public void deleteFile(String bucketName, String objectName) {
        ossClient.removeObject(bucketName, objectName);
    }

    /**
     * 根据链接删除文件
     *
     * @param url
     */
    @Override
    public void deleteFile(String url) {
        ossClient.removeObject(url);
    }
}
