package com.lishunyi.minioservice.service.impl;

import com.lishunyi.minioservice.config.MinioUtil;
import com.lishunyi.minioservice.enums.FileTypeEnum;
import com.lishunyi.minioservice.model.UploadDTO;
import com.lishunyi.minioservice.service.MinioService;
import com.lishunyi.minioservice.utils.OSSUtils;
import io.minio.messages.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MinioServiceImpl implements MinioService {

    @Autowired(required = false)
    private MinioUtil ossClient;

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

    /**
     * 创建指定区域的存储桶
     *
     * @param bucketName 桶名称
     * @param region     区域
     */
    @Override
    public void createBucket(String bucketName, String region) {
        ossClient.makeBucket(bucketName, region);
    }

    /**
     * 获取存储桶集合
     *
     * @return 存储桶集合
     */
    @Override
    public List<String> listBuckets() {
        return ossClient.listBuckets().stream().map(Bucket::name).collect(Collectors.toList());
    }

    /**
     * 获取对象的预签名URL，用来上传数据
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param module     模块名称
     * @return 临时上传URL
     */
    @Override
    public String presignedPutObject(String bucketName, String objectName, String module) {
        String fileSuffix = OSSUtils.getFileSuffix(objectName);
        // 去后缀文件名
        String name = OSSUtils.getFileName(objectName);
        // 获得新文件名
        String fileName = OSSUtils.timeFileName(name);
        // 获取路径类型
        FileTypeEnum fileTypeEnum = OSSUtils.getFileType(fileSuffix);
        // 如果文件没有后缀则不添加
        String pathFileName = "";
        if (StringUtils.isEmpty(fileSuffix)) {
            pathFileName = module + "/" + fileTypeEnum.getCode() + "/" + fileName;
        } else {
            pathFileName = module + "/" + fileTypeEnum.getCode() + "/" + fileName + "." + fileSuffix;
        }

        return ossClient.presignedPutObject(bucketName, pathFileName);
    }

    /**
     * 获取指定存储桶的策略
     *
     * @param bucketName 存储桶名称
     * @return 策略
     */
    @Override
    public String getBucketPolicy(String bucketName) {
        return ossClient.getBucketPolicy(bucketName);
    }
}
