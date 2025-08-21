package com.example.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import com.example.common.api.auth.AuthService;
import com.example.common.api.auth.dto.LoginDTO;
import com.example.common.api.auth.dto.LogoutDTO;
import com.example.common.api.user.UserService;
import com.example.common.api.user.vo.UserInfoVO;
import com.example.common.exception.BusinessException;
import com.example.common.utils.BCryptSalt;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class AuthServiceImpl implements AuthService {
    @DubboReference
    private UserService userService;
    @Override
    public String login(LoginDTO dto) {
        UserInfoVO vo=userService.getUserInfoByEmail(dto.getEmail());

        if (!BCryptSalt.verifyPassword(dto.getPassword(),vo.getPassword(),vo.getSalt())){
            throw new BusinessException("密码错误，请重试");
        }
        StpUtil.login(vo.getId());
        return StpUtil.getTokenInfo().getTokenValue();
    }


    @Override
    public void logout(LogoutDTO dto) {

    }
}
