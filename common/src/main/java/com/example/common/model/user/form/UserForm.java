package com.example.common.model.user.form;

import lombok.Data;

@Data
public class UserForm {
    private Long id;
    private String name;
    private String password;
    private String salt;
    private String avatar;
    private String email;
    private Double balance;
}
