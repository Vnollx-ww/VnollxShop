package com.example.user.service;

import com.example.common.model.user.dto.LoginDTO;
import com.example.common.model.user.dto.LogoutDTO;
import com.example.common.model.user.dto.RegisterDTO;
import com.example.common.model.user.dto.UpdatePasswordDTO;
import com.example.common.model.user.dto.UpdateUserInfoDTO;
import com.example.common.model.user.vo.UserInfoVO;
import lombok.Data;

public interface UserService {
    UserInfoVO getUserInfoById(Long id);
    UserInfoVO getUserInfoByEmail(String email);
    void updateUserInfo(UpdateUserInfoDTO dto);
    void updatePassword(UpdatePasswordDTO dto);
    void register(RegisterDTO dto);
    String login(LoginDTO dto);
    void logout(LogoutDTO dto);
    Double getBalance(Long uid);
    void updateBalance(Long uid,Double cost);
}
