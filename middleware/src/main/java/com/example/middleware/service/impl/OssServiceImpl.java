package com.example.middleware.service.impl;

import com.example.common.model.middleware.oss.UploadFileDTO;
import com.example.common.utils.FileOperation;
import com.example.middleware.service.OssService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class OssServiceImpl implements OssService {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    private static String domain;
    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        // 在初始化时创建MinioClient
        domain=endpoint;
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
    private static final Logger logger = LoggerFactory.getLogger(OssServiceImpl.class);
    @Override
    public String uploadFile(UploadFileDTO dto) {
        String encryptedFileName = FileOperation.encryptFileName(dto.getFileName());
        logger.info("原始文件名: {}, 加密后文件名: {}", dto.getFileName(), encryptedFileName);
        try {

            // 2. 检查存储桶是否存在，不存在则创建
            ensureBucketExists(minioClient, dto.getBucket());
            MultipartFile file=FileOperation.base64ToMultipartFile(dto.getMultipartFile(),dto.getFileName());
            // 3. 获取文件流
            InputStream inputStream = file.getInputStream();
            long fileSize = file.getSize();
            String contentType = file.getContentType();

            // 4. 上传文件
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(dto.getBucket())
                    .object(encryptedFileName+"."+dto.getLastFix())
                    .stream(inputStream, fileSize, -1) // -1表示使用默认分片大小
                    .contentType(contentType)
                    .build();

            minioClient.putObject(putObjectArgs);
            logger.info("文件上传成功: {}/{}", dto.getBucket(), encryptedFileName);
            return generatePublicUrl(dto.getBucket(), encryptedFileName,dto.getLastFix());

        } catch (MinioException e) {
            logger.error("MinIO操作失败: {}", e.getMessage(), e);
            throw new RuntimeException("MinIO操作失败: " + e.getMessage(), e);
        } catch (IOException e) {
            logger.error("文件读写异常: {}", e.getMessage(), e);
            throw new RuntimeException("文件读写异常: " + e.getMessage(), e);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            logger.error("MinIO客户端认证失败: {}", e.getMessage(), e);
            throw new RuntimeException("MinIO客户端认证失败: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("文件上传未知异常: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
    }
    private static void ensureBucketExists(MinioClient minioClient, String bucketName) {
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                logger.info("创建存储桶: {}", bucketName);
            }
        } catch (Exception e) {
            logger.error("检查/创建存储桶失败: {}", e.getMessage(), e);
            throw new RuntimeException("存储桶操作失败: " + e.getMessage(), e);
        }
    }
    private static String generatePublicUrl(String bucket, String encryptedFileName,String lastFix) {
        return domain + "/" + bucket + "/" + encryptedFileName+"."+lastFix;
    }

}
