package com.example.common.model.user.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoVO implements Serializable {
    private Long id;
    private String name;
    private String avatar;
}
