package com.lishunyi.minioservice.service;

import com.lishunyi.minioservice.model.UploadDTO;
import io.minio.messages.Bucket;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    /**
     * 创建指定区域的存储桶
     *
     * @param bucketName 桶名称
     * @param region     区域
     */
    void createBucket(String bucketName, String region);

    /**
     * 获取存储桶集合
     *
     * @return 存储桶集合
     */
    List<String> listBuckets();

    /**
     * 获取对象的预签名URL，用来上传数据
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param module     模块名称
     * @return 临时上传URL
     */
    String presignedPutObject(String bucketName, String objectName, String module);

    /**
     * 获取指定存储桶的策略
     *
     * @param bucketName 存储桶名称
     * @return 策略
     */
    String getBucketPolicy(String bucketName);
}
