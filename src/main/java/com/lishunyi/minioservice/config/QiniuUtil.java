package com.lishunyi.minioservice.config;

import com.lishunyi.minioservice.props.QiniuPorperties;
import com.qiniu.util.Auth;

/**
 * @author 李顺仪
 * @version 1.0
 * @since 2020/8/5 19:19
 **/
public class QiniuUtil {

    private QiniuPorperties porperties;

    private Auth auth;

    public QiniuUtil(QiniuPorperties porperties) {
        this.porperties = porperties;
        this.init();
    }

    private void init() {
        this.auth = Auth.create(this.porperties.getAccessKey(), this.porperties.getSecretKey());
    }

    public String getUploadToken(String bucketName) {
        return auth.uploadToken(bucketName);
    }
}
