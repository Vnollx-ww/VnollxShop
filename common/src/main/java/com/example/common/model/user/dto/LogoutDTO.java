package com.example.common.model.user.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogoutDTO implements Serializable {
    private String token;
}
