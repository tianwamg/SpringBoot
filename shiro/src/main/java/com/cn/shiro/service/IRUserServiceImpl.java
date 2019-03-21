package com.cn.shiro.service;

import com.cn.shiro.dao.UserMapper;
import com.cn.shiro.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IRUserServiceImpl implements IRUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public int addOrUpdate(User user) {
        int n=0;
        if(user.getId()!=null){
            n = userMapper.updateById(user.getId(),user.getUsername(),user.getPassword());
        }else {
            n = userMapper.add(user.getUsername(),user.getPassword());
        }
        return n;
    }

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public int delete(Long id) {
        return userMapper.delete(id);
    }

    public User login(String username){
        return userMapper.findByName(username);
    }
}
