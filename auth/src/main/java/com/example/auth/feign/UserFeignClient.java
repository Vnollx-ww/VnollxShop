package com.example.auth.feign;

import com.example.common.api.user.vo.UserInfoVO;
import com.example.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service",path = "/api/user") // 这里只需要服务名
public interface UserFeignClient {

    @GetMapping("/info/email")
    Result<UserInfoVO> getUserInfoByEmail(@RequestParam String email);

    @GetMapping("/info/id")
    Result<UserInfoVO> getUserInfoById(@RequestParam String id);
}