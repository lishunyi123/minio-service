package com.lishunyi.minioservice.config;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.json.JSONUtil;
import com.lishunyi.minioservice.enums.FileTypeEnum;
import com.lishunyi.minioservice.model.UploadDTO;
import com.lishunyi.minioservice.props.MinioPorperties;
import com.lishunyi.minioservice.utils.OSSUtils;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
public class OSSClient {

    /**
     * minio客户端.
     */
    private MinioClient minioClient;

    /**
     * 配置参数
     */
    private MinioPorperties minioPorperties;

    public OSSClient(MinioPorperties minioPorperties) {
        this.minioPorperties = minioPorperties;
        this.init();
    }

    @SneakyThrows
    private void init() {
        this.minioClient = new MinioClient(minioPorperties.getEndpoint(), minioPorperties.getAccessKey(), minioPorperties.getSecretKey());
    }

    // TODO 所有`catch`部分应该抛出异常

    /**
     * 存储桶不存在则创建
     *
     * @param bucketName
     */
    public void doesBucketExistMake(String bucketName) {
        try {
            boolean exists = this.minioClient.bucketExists(bucketName);
            if (!exists) {
                this.minioClient.makeBucket(bucketName);
            }
        } catch (Exception e) {
            log.error("文件存储桶错误");
        }
    }

    /**
     * 存储桶不存在就抛异常
     *
     * @param bucketName
     */
    public void doesBucketExistError(String bucketName) {
        boolean exists = false;
        try {
            exists = this.minioClient.bucketExists(bucketName);
        } catch (Exception e) {
            log.error("文件存储桶错误");
        }
        if (!exists) {
            throw new NullPointerException("存储桶不存在");
        }
    }

    /**
     * 上传对象
     *
     * @param bucketName
     * @param multipartFile
     * @param uploadDTO
     * @return
     */
    public String putObject(String bucketName, MultipartFile multipartFile, UploadDTO uploadDTO) {
        String filePath = this.buildFilePath(uploadDTO, multipartFile.getOriginalFilename());
        try {
            InputStream inputStream = multipartFile.getInputStream();
            String contentType = multipartFile.getContentType();
            PutObjectOptions putObjectOptions = new PutObjectOptions(inputStream.available(), -1);
            putObjectOptions.setContentType(contentType);
            minioClient.putObject(bucketName, filePath, inputStream, putObjectOptions);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return filePath;
    }

    /**
     * 获取对象路径
     *
     * @param bucketName
     * @param fileName
     * @return
     */
    public String getObjectUrl(String bucketName, String fileName) {
        String objectUrl = "";
        try {
            objectUrl = this.minioClient.getObjectUrl(bucketName, fileName);
        } catch (Exception e) {
            log.error("文件不存在");
        }
        return objectUrl;
    }

    /**
     * 对象移除
     *
     * @param bucketName
     * @param objectName
     */
    public void removeObject(String bucketName, String objectName) {
        try {
            this.minioClient.removeObject(bucketName, objectName);
        } catch (Exception e) {
            log.error("文件删除失败");
        }
    }

    /**
     * 根据url移除文件
     *
     * @param url
     */
    public void removeObject(String url) {
        // 去除链接endpoint前缀
        String endpoint = this.minioPorperties.getEndpoint() + "/";
        String bucketNameUrl = url.substring(endpoint.length());
        // 获取bucketName、objectName
        String bucketName = this.getBucketNameByUrl(bucketNameUrl);
        String objectName = this.getObjectName(bucketNameUrl, bucketName);
        try {
            this.minioClient.removeObject(bucketName, objectName);
        } catch (Exception e) {
            log.error("文件删除失败");
        }
    }

    /**
     * 创建指定区域的存储桶
     *
     * @param bucketName 桶名称
     * @param region     区域
     */
    public void makeBucket(String bucketName, String region) {
        try {
            boolean exists = this.minioClient.bucketExists(bucketName);
            if (exists) {
                throw new NullPointerException("存储桶已存在！");
            }
            this.minioClient.makeBucket(bucketName, region);
        } catch (Exception e) {
            log.error("创建存储桶失败！");
        }
    }

    /**
     * 获取存储桶集合
     *
     * @return 存储桶集合
     */
    public List<Bucket> listBuckets() {
        List<Bucket> buckets = null;
        try {
            buckets = this.minioClient.listBuckets();
        } catch (Exception e) {
        }
        return buckets;
    }

    /**
     * 获取对象的预签名URL，用来上传数据
     *
     * @param bucketName
     * @param objectName
     * @return
     */
    public String presignedPutObject(String bucketName, String objectName) {
        String url = null;
        try {
            url = this.minioClient.presignedPutObject(bucketName, objectName, 60 * 60 * 5);
        } catch (Exception e) {

        }
        return url;
    }

    /**
     * 根据去除endpoint链接获取bucketName
     *
     * @param bucketNameUrl
     * @return
     */
    private String getBucketNameByUrl(String bucketNameUrl) {
        return bucketNameUrl.substring(0, bucketNameUrl.indexOf('/'));
    }

    /**
     * 获取对象名称
     *
     * @param bucketNameUrl
     * @param bucketName
     * @return
     */
    private String getObjectName(String bucketNameUrl, String bucketName) {
        return bucketNameUrl.substring(bucketName.length() + 1);
    }

    /**
     * 构造文件路径
     * 模块/文件类型/新文件名
     * 新文件名：旧文件名 + uuid + 后缀
     *
     * @param uploadDTO
     * @param originFileName
     * @return
     */
    private String buildFilePath(UploadDTO uploadDTO, String originFileName) {
        String fileSuffix = "";
        try {
            /**
             * 获取文件后缀名
             * 先根据文件二进制类型获取
             * 为空就截取后缀名
             */
            fileSuffix = OSSUtils.getType(uploadDTO.getMultipartFile());
        } catch (Exception e) {
            log.error("");
        }
        // 去后缀文件名
        String name = OSSUtils.getFileName(originFileName);
        // 获得新文件名
        String fileName = OSSUtils.timeFileName(name);
        // 获取路径类型
        FileTypeEnum fileTypeEnum = OSSUtils.getFileType(fileSuffix);
        // 如果文件没有后缀则不添加
        if (StringUtils.isEmpty(fileSuffix)) {
            return uploadDTO.getModule() + "/" + fileTypeEnum.getCode() + "/" + fileName;
        }
        return uploadDTO.getModule() + "/" + fileTypeEnum.getCode() + "/" + fileName + "." + fileSuffix;
    }

}
