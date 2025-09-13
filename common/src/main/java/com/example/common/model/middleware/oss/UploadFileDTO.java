package com.example.common.model.middleware.oss;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileDTO {
    private String multipartFile;
    private String bucket;
    private String fileName;
    private String lastFix;
}
