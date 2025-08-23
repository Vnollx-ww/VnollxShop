package com.example.user.controller;

import com.example.common.api.user.dto.RegisterDTO;
import com.example.common.api.user.dto.UpdatePasswordDTO;
import com.example.common.api.user.dto.UpdateUserInfoDTO;
import com.example.common.api.user.vo.UserInfoVO;
import com.example.common.result.Result;
import com.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/info/id")
    public Result<UserInfoVO> getUserInfoById(@RequestParam String id){
        UserInfoVO vo=userService.getUserInfoById(Long.parseLong(id));
        return Result.success(vo);
    }
    @GetMapping("/info/email")
    public Result<UserInfoVO> getUserInfoByEmail(@RequestParam String email){
        UserInfoVO vo=userService.getUserInfoByEmail(email);
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
