package com.example.user.controller;

import com.example.common.model.user.dto.LoginDTO;
import com.example.common.model.user.dto.LogoutDTO;
import com.example.common.model.user.dto.RegisterDTO;
import com.example.common.model.user.dto.UpdatePasswordDTO;
import com.example.common.model.user.dto.UpdateUserInfoDTO;
import com.example.common.model.user.vo.UserInfoVO;
import com.example.common.result.Result;
import com.example.user.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/info/id")
    public UserInfoVO getUserInfoById(@RequestParam String uid){
        return userService.getUserInfoById(Long.parseLong(uid));
    }
    @GetMapping("/info/email")
    public UserInfoVO getUserInfoByEmail(@RequestParam String email){
        return userService.getUserInfoByEmail(email);
    }
    @PutMapping("/update/info")
    public void updateUserInfo(@ModelAttribute  UpdateUserInfoDTO dto){
        userService.updateUserInfo(dto);
    }
    @PutMapping("/update/password")
    public void updatePassword(@ModelAttribute  UpdatePasswordDTO dto){
        userService.updatePassword(dto);
    }
    @PostMapping("/register")
    public void register(@ModelAttribute RegisterDTO dto){
        userService.register(dto);
    }
    @PostMapping("/login")
    public String login(@ModelAttribute LoginDTO dto){
        return userService.login(dto);
    }
    @PostMapping("/logout")
    public void logout(@ModelAttribute LogoutDTO dto){
        userService.logout(dto);
    }
    @GetMapping("/get-balance")
    public Double getBalance(@RequestParam Long uid){
        return userService.getBalance(uid);
    }
    @PutMapping("/update/balance")
    public void updateBalance(@RequestBody Long uid,@RequestBody Double cost){
        userService.updateBalance(uid,cost);
    }
}
