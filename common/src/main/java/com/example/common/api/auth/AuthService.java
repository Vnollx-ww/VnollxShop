package com.example.common.api.auth;

import com.example.common.api.auth.dto.LoginDTO;
import com.example.common.api.auth.dto.LogoutDTO;
import com.example.common.exception.BusinessException;

public interface AuthService {
    String login(LoginDTO dto)throws BusinessException;
    void logout(LogoutDTO dto)throws BusinessException;
}
