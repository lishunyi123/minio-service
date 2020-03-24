package com.lishunyi.minioservice.config;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.api.client.util.Lists;
import com.lishunyi.minioservice.enums.FileTypeEnum;
import com.lishunyi.minioservice.model.UploadDTO;
import com.lishunyi.minioservice.props.MinioPorperties;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
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

    public FileTypeEnum getFileType(String fileSuffix) {
        // 如果文件后缀为空返回默认文件
        if (StringUtils.isEmpty(fileSuffix)) {
            return FileTypeEnum.DEFAULT;
        }
        FileReader fileReader = new FileReader("static/file-type.json");
        String readString = fileReader.readString();
        Map<String, Object> map = JSONUtil.parseObj(readString);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            List list = (List) entry.getValue();
            if (list.contains(fileSuffix)) {
                return FileTypeEnum.valuesOf(entry.getKey());
            }
        }
        return FileTypeEnum.DEFAULT;
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
        String fileSuffix = "";
        try {
            /**
             * 获取文件后缀名
             * 先根据文件二进制类型获取
             * 为空就截取后缀名
             */
            fileSuffix = this.getType(uploadDTO.getMultipartFile());
        } catch (Exception e) {
            log.error("");
        }
        // 去后缀文件名
        String name = this.getFileName(originFileName);
        // 获得新文件名
        String fileName = this.timeFileName(name);
        // 获取路径类型
        FileTypeEnum fileTypeEnum = this.getFileType(fileSuffix);
        // 如果文件没有后缀则不添加
        if (StringUtils.isEmpty(fileSuffix)) {
            return uploadDTO.getModule() + "/" + fileTypeEnum.getCode() + "/" + fileName;
        }
        return uploadDTO.getModule() + "/" + fileTypeEnum.getCode() + "/" + fileName + "." + fileSuffix;
    }

    /**
     * uuid构造文件名称
     *
     * @param originFileName
     * @return
     */
    private String uuidFileName(String originFileName) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        if (originFileName.length() > 30) {
            return uuid;
        }
        return originFileName + "_" + uuid;
    }

    /**
     * 时间戳构造文件名
     *
     * @param originFileName
     * @return
     */
    private String timeFileName(String originFileName) {
        // 毫秒级时间戳
        String str = String.valueOf(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        if (originFileName.length() > 30) {
            return str;
        }
        return originFileName + "_" + str;
    }

    /**
     * 获取文件名后缀
     * 不带.
     *
     * @param originFileName
     * @return
     */
    private String getFileSuffix(String originFileName) {
        if (originFileName.contains(".")) {
            return originFileName.substring(originFileName.lastIndexOf('.') + 1).toLowerCase();
        }
        return null;
    }

    /**
     * 获取文件类型
     *
     * @param inputStream
     * @return
     */
    private String getFileType(InputStream inputStream) {
        return FileTypeUtil.getType(inputStream);
    }

    /**
     * 获取类型
     * 仿照hutool里面的`FileTypeUtil.getType`
     *
     * @param multipartFile
     * @return
     * @throws IORuntimeException
     * @throws IOException
     */
    public String getType(MultipartFile multipartFile) throws IORuntimeException, IOException {
        String typeName;
        FileInputStream in = null;
        try {
            in = (FileInputStream) multipartFile.getInputStream();
            typeName = FileTypeUtil.getType(in);
        } finally {
            IoUtil.close(in);
        }

        if (null == typeName) {
            // 未成功识别类型，扩展名辅助识别
            typeName = this.getFileSuffix(multipartFile.getOriginalFilename());
        } else if ("xls".equals(typeName)) {
            // xls、doc、msi的头一样，使用扩展名辅助判断
            final String extName = this.getFileSuffix(multipartFile.getOriginalFilename());
            if ("doc".equalsIgnoreCase(extName)) {
                typeName = "doc";
            } else if ("msi".equalsIgnoreCase(extName)) {
                typeName = "msi";
            }
        } else if ("zip".equals(typeName)) {
            // zip可能为docx、xlsx、pptx、jar、war等格式，扩展名辅助判断
            final String extName = this.getFileSuffix(multipartFile.getOriginalFilename());
            if ("docx".equalsIgnoreCase(extName)) {
                typeName = "docx";
            } else if ("xlsx".equalsIgnoreCase(extName)) {
                typeName = "xlsx";
            } else if ("pptx".equalsIgnoreCase(extName)) {
                typeName = "pptx";
            } else if ("jar".equalsIgnoreCase(extName)) {
                typeName = "jar";
            } else if ("war".equalsIgnoreCase(extName)) {
                typeName = "war";
            }
        }
        return typeName;
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
