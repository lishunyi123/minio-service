package com.lishunyi.minioservice.model;

import lombok.Data;

/**
 * @author 李顺仪
 * @version 1.0
 * @since 2020/6/24 17:16
 **/
@Data
public class BucketDTO {

    /**
     * 桶名称.
     */
    private String bucketName;

    /**
     * 区域.
     */
    private String region;
}
