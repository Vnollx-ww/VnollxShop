package com.example.common.api.user.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateUserInfoDTO implements Serializable {
    private Long id;
    private String name;
    private String avatar;
}
