package com.example.user.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.common.model.user.dto.*;
import com.example.common.model.user.vo.UserInfoVO;
import com.example.common.result.Result;
import com.example.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/info/id")
    public Result<UserInfoVO> getUserInfoById(
            @RequestParam(required = false) String uid,
            HttpServletRequest request
    ){
            if (StringUtils.isBlank(uid)){
                Long uuid = Long.parseLong(request.getHeader("X-User-Id"));
                UserInfoVO userInfo = userService.getUserInfoById(uuid);
                return Result.success(userInfo);
            }
            UserInfoVO userInfo = userService.getUserInfoById(Long.parseLong(uid));
            return Result.success(userInfo);
    }

    @GetMapping("/info/email")
    public Result<UserInfoVO> getUserInfoByEmail(@RequestParam String email){
            UserInfoVO userInfo = userService.getUserInfoByEmail(email);
            return Result.success(userInfo);
    }

    @PutMapping("/update/info")
    public Result<UserInfoVO> updateUserInfo(@ModelAttribute UpdateUserInfoDTO dto, HttpServletRequest request){
            Long uid = Long.parseLong(request.getHeader("X-User-Id"));
            UserInfoVO updatedUserInfo = userService.updateUserInfo(dto, uid);
            return Result.success(updatedUserInfo);
    }

    @PutMapping("/update/password")
    public Result<Void> updatePassword(@ModelAttribute UpdatePasswordDTO dto, HttpServletRequest request){
            Long uid = Long.parseLong(request.getHeader("X-User-Id"));
            userService.updatePassword(dto, uid);
            return Result.success();
    }

    @PostMapping("/register")
    public Result<Void> register(@ModelAttribute RegisterDTO dto){
            userService.register(dto);
            return Result.success();
    }

    @PostMapping("/login")
    public Result<String> login(@ModelAttribute LoginDTO dto){
            String token = userService.login(dto);
            return Result.success(token);
    }

    @PostMapping("/logout")
    public Result<Void> logout(@ModelAttribute LogoutDTO dto){
            userService.logout(dto);
            return Result.success();
    }

    @GetMapping("/get-balance")
    public Result<Double> getBalance(@RequestParam Long uid){
            Double balance = userService.getBalance(uid);
            return Result.success(balance);
    }

    @PutMapping("/update/balance")
    public Result<Void> updateBalance(@RequestBody UpdateBalanceDTO dto){
            userService.updateBalance(dto);
            return Result.success();
    }

    @PutMapping("/recharge")
    public Result<Void> recharge(@RequestParam Double amount, HttpServletRequest request){
            Long uid = Long.parseLong(request.getHeader("X-User-Id"));
            userService.recharge(amount, uid);
            return Result.success();
    }
}
