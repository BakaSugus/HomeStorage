package cn.j.netstorage.Config;

import cn.j.netstorage.Entity.User.Permission;
import cn.j.netstorage.Entity.User.Role;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ShiroRealm extends AuthorizingRealm {
    @Resource
    UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        if (principalCollection.getPrimaryPrincipal() == null)
            return null;
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        Set<Role> set=userService.getRole(principalCollection.getPrimaryPrincipal().toString());
        Set<String> stringSet=new HashSet<>();
        Set<String> permissions=new HashSet<>();
        for (Role s:set){
            stringSet.add(s.getName());
            for (Permission permission:s.getPermission()) {
                permissions.add(permission.getName());
            }
        }
        simpleAuthorizationInfo.setRoles(stringSet);
        simpleAuthorizationInfo.setStringPermissions(permissions);
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        if (authenticationToken.getPrincipal() == null)
            return null;
        String name = authenticationToken.getPrincipal().toString();
        String password = new String((char[]) authenticationToken.getCredentials());
        User user = userService.getUser(name);
        if (user == null) {
            throw new IncorrectCredentialsException("登录失败，用户名或密码错误");
        }
        //验证账户信息
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(
                user.getEmailAccount(),
                user.getPassword(),
                ByteSource.Util.bytes(user.getEmailAccount()),
                getName());
        return simpleAuthenticationInfo;
    }
}
