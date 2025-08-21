package com.example.gateway.controller;



import com.example.common.api.auth.AuthService;
import com.example.common.api.auth.dto.LoginDTO;
import com.example.common.api.auth.dto.LogoutDTO;
import com.example.common.result.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")

public class AuthController {
    @DubboReference
    private AuthService authService;

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
