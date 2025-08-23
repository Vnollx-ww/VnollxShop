package com.example.user.service;

import com.example.common.api.user.dto.RegisterDTO;
import com.example.common.api.user.dto.UpdatePasswordDTO;
import com.example.common.api.user.dto.UpdateUserInfoDTO;
import com.example.common.api.user.vo.UserInfoVO;
import com.example.common.exception.BusinessException;

public interface UserService {
    UserInfoVO getUserInfoById(Long id)throws BusinessException;
    UserInfoVO getUserInfoByEmail(String email)throws BusinessException;
    void updateUserInfo(UpdateUserInfoDTO dto)throws BusinessException;
    void updatePassword(UpdatePasswordDTO dto)throws BusinessException;
    void register(RegisterDTO dto)throws BusinessException;
}
