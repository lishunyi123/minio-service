package com.lishunyi.minioservice.service.impl;

import com.lishunyi.minioservice.service.MinioService;
import io.minio.MinioClient;
import io.minio.ServerSideEncryption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.KeyGenerator;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class MinioServiceImpl implements MinioService {

    @Autowired
    private MinioClient minioClient;

    /**
     * 文件上传
     *
     * @param backetName    存储桶
     * @param multipartFile 文件流
     * @return
     */
    @Override
    public String uploadFile(String backetName, MultipartFile multipartFile) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String fileName = multipartFile.getOriginalFilename() + "_" + uuid;
        String url = null;
        try {
            // 1.存储桶是否存在
            if (!minioClient.bucketExists(backetName)) {
                minioClient.makeBucket(backetName);
            }
            InputStream inputStream = multipartFile.getInputStream();
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/octet-stream");
            minioClient.putObject(backetName, fileName, inputStream, inputStream.available(), headerMap);
            inputStream.close();
            url = minioClient.presignedGetObject(backetName, fileName);
        } catch (Exception e) {
            log.error("上传文件失败：{}", e.getMessage());
        }

        return url;
    }
}
