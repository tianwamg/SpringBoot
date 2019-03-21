package com.cn.shiro.service;


import com.cn.shiro.domain.User;

public interface IRUserService {

    int addOrUpdate(User user);
    //
    User findById(Long id);

    int delete(Long id);

    User login(String username);
}
