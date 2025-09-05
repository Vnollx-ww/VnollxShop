package com.example.common.model.user.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdatePasswordDTO implements Serializable {
    private Long id;
    private String oldPwd;
    private String newPwd;
}
