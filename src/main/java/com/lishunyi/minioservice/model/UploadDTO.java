package com.lishunyi.minioservice.model;

import com.lishunyi.minioservice.enums.FileTypeEnum;
import lombok.Data;

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
    private FileTypeEnum fileTypeEnum;
}
