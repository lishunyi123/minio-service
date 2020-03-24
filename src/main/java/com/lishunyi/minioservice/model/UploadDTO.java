package com.lishunyi.minioservice.model;

import com.lishunyi.minioservice.enums.FileTypeEnum;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author LSY
 * 文件上传对象
 */
@Data
public class UploadDTO {

    /**
     * 模块.
     */
    private String module;

    /**
     * 文件类型.
     */
    private FileTypeEnum fileTypeEnum = FileTypeEnum.DEFAULT;

    /**
     * 文件流
     */
    private MultipartFile multipartFile;
}
