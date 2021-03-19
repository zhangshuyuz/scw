package com.yuu.scw.project.component;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Slf4j
@Data
public class OssTemplate {

    String endpoint;

    String accessKeyId;

    String accessKeySecret;

    String bucketName;

    String fileUploadPath;

    public OssTemplate() {}

    public String upload(InputStream fileInputStream, String fileName) {

        try {
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 上传文件流。
            ossClient.putObject(bucketName, fileUploadPath + fileName, fileInputStream);

            // 关闭OSSClient。
            ossClient.shutdown();

            String filePath = "https://" + bucketName + "." + endpoint + "/" + fileUploadPath + fileName;

            log.debug("文件上传成功-{}", filePath);

            return filePath;
        } catch (Exception e) {
            e.printStackTrace();

            log.error("文件上传失败-{}", e.getMessage());

            return null;
        }

    }

}
