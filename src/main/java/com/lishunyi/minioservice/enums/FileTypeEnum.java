package com.lishunyi.minioservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author LSY
 * 文件类型枚举
 */
@NoArgsConstructor
@AllArgsConstructor
public enum FileTypeEnum {

    /**
     * 默认.
     */
    DEFAULT("default"),

    /**
     * HTML文档.
     */
    HTML("html"),

    /**
     * 图片.
     */
    IMAGE("image"),

    /**
     * 音频.
     */
    AUDIO("audio"),

    /**
     * 视频.
     */
    VIDEO("video"),

    /**
     * 办公文件
     * word、excel、ppt
     */
    OFFICE("office"),

    /**
     * JSON.
     */
    JSON("json"),

    /**
     * PDF.
     */
    PDF("pdf"),

    /**
     * 压缩文件，不仅仅只是zip格式.
     */
    ZIP("zip");

    @Getter
    private String code;
}
