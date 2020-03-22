package com.lishunyi.minioservice.config;

import com.lishunyi.minioservice.props.MinioPorperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioPorperties.class)
public class MinioConfiguration {

    @Autowired
    private MinioPorperties minioPorperties;

    @Bean
    public OSSClient minioClient() {
        return new OSSClient(minioPorperties);
    }
}
