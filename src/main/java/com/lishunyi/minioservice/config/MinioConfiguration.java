package com.lishunyi.minioservice.config;

import com.lishunyi.minioservice.props.MinioPorperties;
import com.lishunyi.minioservice.props.QiniuPorperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    @Configuration
    @ConditionalOnProperty(name = "minio.enable", havingValue = "true", matchIfMissing = false)
    @EnableConfigurationProperties(MinioPorperties.class)
    static class MinioListener {

        @Bean
        @ConditionalOnMissingBean(MinioUtil.class)
        public MinioUtil minioUtil(MinioPorperties minioPorperties) {
            return new MinioUtil(minioPorperties);
        }

    }

    @Configuration
    @ConditionalOnProperty(name = "qiniu.enable", havingValue = "true", matchIfMissing = false)
    @EnableConfigurationProperties(QiniuPorperties.class)
    static class QiniuListener {

        @Bean
        @ConditionalOnMissingBean(QiniuUtil.class)
        public QiniuUtil qiniuUtil(QiniuPorperties qiniuPorperties) {
            return new QiniuUtil(qiniuPorperties);
        }
    }


}
