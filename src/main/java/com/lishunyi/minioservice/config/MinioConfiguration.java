package com.lishunyi.minioservice.config;

import com.lishunyi.minioservice.props.MinioPorperties;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioPorperties.class)
public class MinioConfiguration {

    @Autowired
    private MinioPorperties minioPorperties;

    @Bean
    public OSSClient minioClient() throws InvalidPortException, InvalidEndpointException {
        return new OSSClient(minioPorperties);
    }
}
