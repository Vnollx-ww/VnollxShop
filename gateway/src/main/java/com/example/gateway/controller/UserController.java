package com.example.gateway.controller;

import com.example.common.api.user.UserService;
import com.example.common.api.user.dto.RegisterDTO;
import com.example.common.api.user.dto.UpdatePasswordDTO;
import com.example.common.api.user.dto.UpdateUserInfoDTO;
import com.example.common.api.user.vo.UserInfoVO;
import com.example.common.result.Result;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @DubboReference
    private UserService userService;
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo(@ModelAttribute  String id){
        UserInfoVO vo=userService.getUserInfoById(Long.parseLong(id));
        return Result.success(vo);
    }
    @PutMapping("/update/info")
    public Result<Void> updateUserInfo(@ModelAttribute  UpdateUserInfoDTO dto){
        userService.updateUserInfo(dto);
        return Result.success();
    }
    @PutMapping("/update/password")
    public Result<Void> updatePassword(@ModelAttribute  UpdatePasswordDTO dto){
        userService.updatePassword(dto);
        return Result.success();
    }
    @PostMapping("/register")
    public Result<Void> register(@ModelAttribute RegisterDTO dto){
        userService.register(dto);
        return Result.success();
    }
}
