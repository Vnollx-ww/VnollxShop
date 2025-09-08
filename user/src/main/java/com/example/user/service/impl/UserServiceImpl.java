package com.example.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.model.user.dto.*;
import com.example.common.model.user.vo.UserInfoVO;
import com.example.common.exception.BusinessException;
import com.example.common.utils.BCryptSalt;
import com.example.common.utils.JwtToken;
import com.example.user.entity.User;
import com.example.user.mapper.UserMapper;
import com.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
    @Override
    public UserInfoVO getUserInfoById(Long uid) {
        User user=this.getById(uid);
        if (user==null){
            throw new BusinessException("用户Id不存在");
        }
        UserInfoVO vo=new UserInfoVO();
        BeanUtils.copyProperties(user,vo);
        return vo;
    }

    @Override
    public UserInfoVO getUserInfoByEmail(String email) {
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("email",email);
        User user=this.getOne(wrapper);
        if (user==null){
            throw new BusinessException("邮箱地址不存在");
        }
        UserInfoVO vo=new UserInfoVO();
        BeanUtils.copyProperties(user,vo);
        return vo;
    }

    @Override
    public void updateUserInfo(UpdateUserInfoDTO dto) {
        User user=this.getById(dto.getId());
        if (user==null){
            throw new BusinessException("用户id不存在");
        }
        user.setName(dto.getName());
        user.setAvatar(dto.getAvatar());
        this.updateById(user);
    }

    @Override
    public void updatePassword(UpdatePasswordDTO dto) {
        User user=this.getById(dto.getId());
        if (user==null){
            throw new BusinessException("用户id不存在");
        }
        if (!BCryptSalt.verifyPassword(dto.getOldPwd(),user.getPassword(),user.getSalt())){
            throw new BusinessException("旧密码错误");
        }
        //对新密码进行盐值加密
        String hashedNewPwd=BCryptSalt.hashPasswordWithSalt(dto.getNewPwd(),user.getSalt());
        user.setPassword(hashedNewPwd);
        this.updateById(user);
    }

    @Override
    public void register(RegisterDTO dto){
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("email",dto.getEmail());
        User user=this.getOne(wrapper);
        if (user!=null){
            throw new BusinessException("邮箱地址已存在");
        }
        user=new User();
        user.setSalt(BCryptSalt.generateSalt());
        user.setName(dto.getName());
        user.setPassword(BCryptSalt.hashPasswordWithSalt(dto.getPassword(),user.getSalt()));
        user.setEmail(dto.getEmail());

        this.save(user);
    }

    @Override
    public String login(LoginDTO dto)  {
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("email",dto.getEmail());
        User user=this.getOne(wrapper);
        if (user==null){
            throw new BusinessException("邮箱不存在");
        }
        if (!BCryptSalt.verifyPassword(dto.getPassword(),user.getPassword(),user.getSalt())){
            throw new BusinessException("密码错误，请重试");
        }
        return JwtToken.generateToken(String.valueOf(user.getId()),"");
    }

    @Override
    public void logout(LogoutDTO dto)  {

    }

    @Override
    public Double getBalance(Long uid) {
        User user=this.getById(uid);
        if (user==null){
            throw new BusinessException("用户不存在");
        }
        return user.getBalance();
    }

    @Override
    public void updateBalance(UpdateBalanceDTO dto) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, Long.parseLong(dto.getUid()))
                .setSql("balance = balance + " + dto.getCost());

        this.update(null, wrapper);
    }
}
