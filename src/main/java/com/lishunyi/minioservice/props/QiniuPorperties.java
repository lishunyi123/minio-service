package com.lishunyi.minioservice.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 李顺仪
 * @version 1.0
 * @since 2020/8/3 18:02
 **/
@ConfigurationProperties(prefix = "qiniu")
@Data
public class QiniuPorperties {
    private Boolean enable;

    private String endpoint;

    private String accessKey;

    private String secretKey;
}
