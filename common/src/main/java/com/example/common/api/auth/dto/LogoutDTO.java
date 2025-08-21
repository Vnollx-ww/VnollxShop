package com.example.common.api.auth.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogoutDTO implements Serializable {
    private String token;
}
