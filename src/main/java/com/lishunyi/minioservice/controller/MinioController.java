package com.lishunyi.minioservice.controller;

import com.lishunyi.minioservice.model.BucketDTO;
import com.lishunyi.minioservice.model.UploadDTO;
import com.lishunyi.minioservice.service.MinioService;
import io.minio.messages.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@RestController
public class MinioController {

    @Autowired
    private MinioService minioService;

    public static void main(String[] args) {
        try {
            File file = new File("C:\\Users\\LSY\\Desktop\\abcd.txt");
            URL url = new URL("http://127.0.0.1:9000/test/user/default/abc_1593004538917.txt?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20200624%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20200624T131538Z&X-Amz-Expires=18000&X-Amz-SignedHeaders=host&X-Amz-Signature=ce8cae0c0e6b265ec51fabd0aad76d7f389e61a1dfc284f8f716135e8f17b48f");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "text/plain");
            OutputStream out = new DataOutputStream(connection.getOutputStream());
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] buf = new byte[20048];
            while ((bytes = in.read(buf)) != -1) {
                out.write(buf, 0, bytes);
            }
            in.close();
            out.flush();
            connection.getResponseCode();
            System.out.println(connection.getResponseCode());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 文件上传
     *
     * @param bucketName 桶名称
     * @param uploadDTO  上传参数
     * @return 文件路径
     */
    @PostMapping("upload/{bucket-name}")
    public String uploadFile(@PathVariable(value = "bucket-name") String bucketName, UploadDTO uploadDTO) {
        return minioService.uploadFile(bucketName, uploadDTO.getMultipartFile(), uploadDTO);
    }

    /**
     * 根据桶以及路径删除
     *
     * @param bucketName 桶名称
     * @param url        文件路径
     */
    @DeleteMapping("delete/{bucket-name}")
    public void deleteFile(@PathVariable(value = "bucket-name") String bucketName, @RequestParam("url") String url) {
        minioService.deleteFile(bucketName, url);
    }

    /**
     * 根据路径删除
     *
     * @param url 文件路径
     */
    @DeleteMapping("delete")
    public void deleteFileByUrl(@RequestParam("url") String url) {
        minioService.deleteFile(url);
    }

    /**
     * 创建存储桶
     *
     * @param bucket 存储桶对象
     */
    @PostMapping("create-bucket")
    public void createBucket(@RequestBody BucketDTO bucket) {
        minioService.createBucket(bucket.getBucketName(), bucket.getRegion());
    }

    /**
     * 获取所有存储桶
     *
     * @return 存储桶列表
     */
    @GetMapping("list-buckets")
    public List<String> listBuckets() {
        return minioService.listBuckets();
    }

    @GetMapping("pre-signed-putObject")
    public String presignedPutObject(@RequestParam(value = "bucketName") String bucketName,
                                     @RequestParam(value = "objectName") String objectName,
                                     @RequestParam(value = "module") String module) {
        return minioService.presignedPutObject(bucketName, objectName, module);
    }
}
