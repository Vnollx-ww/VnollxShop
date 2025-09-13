package com.example.middleware.service;

import com.example.common.model.middleware.oss.UploadFileDTO;
import org.springframework.web.multipart.MultipartFile;

public interface OssService {
     String uploadFile(UploadFileDTO dto);
}
