package com.example.common.feign;

import com.example.common.model.user.dto.*;
import com.example.common.model.user.vo.UserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service",path = "/api/user") // 这里只需要服务名
public interface UserFeignClient {
    @GetMapping("/info/id")
    UserInfoVO getUserInfoById(@RequestParam String uid);
    @GetMapping("/info/email")
    UserInfoVO getUserInfoByEmail(@RequestParam String email);
    @PutMapping("/update/info")
    void updateUserInfo(@ModelAttribute UpdateUserInfoDTO dto);
    @PutMapping("/update/password")
    void updatePassword(@ModelAttribute UpdatePasswordDTO dto);
    @PostMapping("/register")
    void register(@ModelAttribute RegisterDTO dto);
    @PostMapping("/login")
    String login(@ModelAttribute LoginDTO dto);
    @PostMapping("/logout")
    void logout(@ModelAttribute LogoutDTO dto);
    @GetMapping("/get-balance")
    Double getBalance(@RequestParam Long uid);
    @PutMapping("/update/balance")
    void updateBalance(@RequestBody Long uid,@RequestBody Double cost);
}
