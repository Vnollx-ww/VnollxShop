package com.example.product.feign;

import com.example.common.model.user.dto.*;
import com.example.common.model.user.vo.UserInfoVO;
import com.example.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", path = "/api/user")
public interface UserFeignClient {

    @GetMapping("/info/id")
    Result<UserInfoVO> getUserInfoById(@RequestParam String uid);

    @GetMapping("/info/email")
    Result<UserInfoVO> getUserInfoByEmail(@RequestParam String email);

    @PutMapping("/update/info")
    Result<Void> updateUserInfo(@ModelAttribute UpdateUserInfoDTO dto);

    @PutMapping("/update/password")
    Result<Void> updatePassword(@ModelAttribute UpdatePasswordDTO dto);

    @PostMapping("/register")
    Result<Void> register(@ModelAttribute RegisterDTO dto);

    @PostMapping("/login")
    Result<String> login(@ModelAttribute LoginDTO dto);

    @PostMapping("/logout")
    Result<Void> logout(@ModelAttribute LogoutDTO dto);

    @GetMapping("/get-balance")
    Result<Double> getBalance(@RequestParam Long uid);

    @PutMapping("/update/balance")
    Result<Void> updateBalance(@RequestBody UpdateBalanceDTO dto);
}