package com.lishunyi.minioservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

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
     * XML.
     */
    XML("xml"),

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

    public static FileTypeEnum valuesOf(final String code) {
        return Arrays.stream(FileTypeEnum.values())
                .filter(e -> Objects.equals(e.getCode(), code))
                .findFirst().orElseThrow(() -> new NullPointerException("没有与 [" + code + "] 匹配的类型"));
    }
}
