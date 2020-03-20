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
