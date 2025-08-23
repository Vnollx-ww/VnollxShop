package com.example.auth.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.example.auth.service.AuthService;
import com.example.common.api.auth.dto.LoginDTO;
import com.example.common.api.auth.dto.LogoutDTO;
import com.example.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public Result<String> login(@ModelAttribute  LoginDTO dto){
        String token=authService.login(dto);
        return Result.success(token);
    }
    @PostMapping("/logout")
    public Result<Void> logout(@ModelAttribute LogoutDTO dto){
        authService.logout(dto);
        return Result.success();
    }
}
