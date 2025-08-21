package com.example.common.api.user.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterDTO implements Serializable {
    private String name;
    private String password;
    private String email;
}
