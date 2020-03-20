package com.lishunyi.minioservice;

import com.lishunyi.minioservice.config.MinioConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MinioConfiguration.class)
public class MinioServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinioServiceApplication.class, args);
    }

}
