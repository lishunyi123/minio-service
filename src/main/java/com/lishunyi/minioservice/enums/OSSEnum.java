package com.lishunyi.minioservice.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author 李顺仪
 * @version 1.0
 * @since 2020/8/5 16:57
 **/
public enum OSSEnum {

    /**
     * minioOSS.
     */
    MINIO,

    /**
     * 阿里云OSS.
     */
    ALIYUN,

    /**
     * 七牛云OSS.
     */
    QINIU;

    public static OSSEnum valuesOf(final String name) {
        return Arrays.stream(OSSEnum.values())
                .filter(e -> Objects.equals(e.name(), name))
                .findFirst().orElseThrow(() -> new NullPointerException("没有与 [" + name + "] 匹配的类型"));
    }
}
