package com.example.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.api.user.dto.RegisterDTO;
import com.example.common.api.user.dto.UpdatePasswordDTO;
import com.example.common.api.user.dto.UpdateUserInfoDTO;
import com.example.common.api.user.vo.UserInfoVO;
import com.example.common.exception.BusinessException;
import com.example.common.utils.BCryptSalt;
import com.example.user.entity.User;
import com.example.user.mapper.UserMapper;
import com.example.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
    @Override
    public UserInfoVO getUserInfoById(Long id) {
        User user=this.getById(id);
        if (user==null){
            throw new BusinessException("用户id不存在");
        }
        UserInfoVO vo=new UserInfoVO();
        BeanUtils.copyProperties(user,vo,"password","balance","email","salt");
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
    public void register(RegisterDTO dto) throws BusinessException {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("email",dto.getEmail());
        if (this.count(queryWrapper)>0){
            throw new BusinessException("邮箱已存在");
        }
        User user=new User();
        user.setSalt(BCryptSalt.generateSalt());
        user.setName(dto.getName());
        user.setPassword(BCryptSalt.hashPasswordWithSalt(dto.getPassword(),user.getSalt()));
        user.setEmail(dto.getEmail());
        this.save(user);
    }

    @Override
    public UserInfoVO getUserInfoByEmail(String email) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("email",email);
        User user=this.getOne(queryWrapper);
        if (user==null){
            throw new BusinessException("邮箱不存在");
        }
        UserInfoVO vo=new UserInfoVO();
        BeanUtils.copyProperties(user,vo,"balance","email");
        System.out.println(vo.getId());
        System.out.println(vo.getName());
        System.out.println(vo.getPassword());
        return vo;
    }
}
