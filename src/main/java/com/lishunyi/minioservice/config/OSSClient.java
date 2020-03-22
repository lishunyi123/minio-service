package com.lishunyi.minioservice.config;

import com.lishunyi.minioservice.model.UploadDTO;
import com.lishunyi.minioservice.props.MinioPorperties;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/octet-stream");
            minioClient.putObject(bucketName, filePath, inputStream, inputStream.available(), headerMap);
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
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileSuffix = this.getFileSuffix(originFileName);
        String name = this.getFileName(originFileName);
        return uploadDTO.getModule() + "/" + uploadDTO.getFileTypeEnum().getCode() + "/" + name + uuid + fileSuffix;
    }

    /**
     * 获取文件名后缀
     *
     * @param originFileName
     * @return
     */
    private String getFileSuffix(String originFileName) {
        if (originFileName.contains(".")) {
            return originFileName.substring(originFileName.lastIndexOf('.')).toLowerCase();
        }
        return null;
    }

    /**
     * 获取去除后缀的文件名
     *
     * @param originFileName
     * @return
     */
    private String getFileName(String originFileName) {
        if (!originFileName.contains(".")) {
            return originFileName;
        }
        return originFileName.substring(0, originFileName.lastIndexOf('.'));
    }
}
