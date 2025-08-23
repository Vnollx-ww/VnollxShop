package com.example.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.example.auth.feign.UserFeignClient;
import com.example.auth.service.AuthService;
import com.example.common.api.auth.dto.LoginDTO;
import com.example.common.api.auth.dto.LogoutDTO;
import com.example.common.api.user.vo.UserInfoVO;
import com.example.common.exception.BusinessException;
import com.example.common.utils.BCryptSalt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserFeignClient userFeignClient;
    @Override
    public String login(LoginDTO dto) {
        UserInfoVO vo=userFeignClient.getUserInfoByEmail(dto.getEmail()).getData();
        if (!BCryptSalt.verifyPassword(dto.getPassword(),vo.getPassword(),vo.getSalt())){
            throw new BusinessException("密码错误，请重试");
        }
        StpUtil.login(vo.getId());
        return StpUtil.getTokenValue();
    }


    @Override
    public void logout(LogoutDTO dto) {
        StpUtil.logout();
    }
}
