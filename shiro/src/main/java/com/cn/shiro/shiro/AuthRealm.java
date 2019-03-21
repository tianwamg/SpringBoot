package com.cn.shiro.shiro;

import com.cn.shiro.domain.User;
import com.cn.shiro.service.IRUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;


public class AuthRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(AuthRealm.class);


    @Autowired
    private IRUserService irUserService;
    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        logger.info("---------------- 执行 Shiro 凭证认证 ----------------------");
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String name = token.getUsername();
        String password = String.valueOf(token.getPassword());
        User user = new User();
        user.setUsername(name);
        user.setPassword(password);
        // 从数据库获取对应用户名密码的用户
        User userList = irUserService.login(user.getUsername() );
        if (userList != null) {
            // 用户为禁用状态
            /*if (userList.getUserEnable() != 1) {
                throw new DisabledAccountException();
            }*/
            logger.info("---------------- Shiro 凭证认证成功 ----------------------");
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                    userList, //用户
                    userList.getPassword(), //密码
                    getName()  //realm name
            );
            return authenticationInfo;
        }
        throw new UnknownAccountException();
    }

    /**
     * 授权
     * @param pricipalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pricipalCollection){
        logger.info("------------------执行shiro授权-------------------------");
        Object principal = pricipalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo  authenticationInfo = new SimpleAuthorizationInfo();
        if( principal instanceof User){
            User user = (User) principal;
            Set<String> roles = new HashSet<>();
            authenticationInfo.addRole("ddd");
            authenticationInfo.addStringPermission("ddd");
            return authenticationInfo;
        }
        //返回null的话，就会导致任何用户访问被拦截的请求时，都会自动跳转到unauthorizedUrl指定的地址
        return null;
    }

    public void initCredentialsMatcher(){
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
    }

}
