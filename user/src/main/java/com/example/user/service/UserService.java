package com.example.user.service;

import com.example.common.model.user.dto.*;
import com.example.common.model.user.vo.UserInfoVO;
import lombok.Data;

public interface UserService {
    UserInfoVO getUserInfoById(Long id);
    UserInfoVO getUserInfoByEmail(String email);
    UserInfoVO updateUserInfo(UpdateUserInfoDTO dto,Long uid);
    void updatePassword(UpdatePasswordDTO dto,Long uid);
    void register(RegisterDTO dto);
    String login(LoginDTO dto);
    void logout(LogoutDTO dto);
    Double getBalance(Long uid);
    void updateBalance(UpdateBalanceDTO dto);
    void recharge(Double amount,Long uid);
}
