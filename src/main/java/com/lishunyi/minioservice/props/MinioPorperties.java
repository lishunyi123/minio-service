package com.lishunyi.minioservice.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
@Data
public class MinioPorperties {

    private String endpoint;

    private String accessKey;

    private String secretKey;
}
