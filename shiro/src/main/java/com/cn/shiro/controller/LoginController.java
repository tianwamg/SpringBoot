package com.cn.shiro.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "登录模块",description = "登录模块")
@RestController
@RequestMapping(value = "/boot/shiro/web")
public class LoginController {

    @ApiOperation(value = "登录操作",notes = "登录操作接口",httpMethod = "POST",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/login",produces = "application/json;charset=utf8")
    public String Login(String name,String password){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","200");
        jsonObject.put("status","200");
        return jsonObject.toJSONString();
    }
}
