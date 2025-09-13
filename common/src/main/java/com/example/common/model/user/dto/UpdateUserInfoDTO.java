package com.example.common.model.user.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class UpdateUserInfoDTO{
    private String name;
    private MultipartFile avatar;
}
