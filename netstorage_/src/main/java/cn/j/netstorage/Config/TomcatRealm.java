package cn.j.netstorage.Config;

import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.UserService;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.Collections;

@Component
public class TomcatRealm extends RealmBase {
    @Autowired
    private UserService userService;

    @Override
    protected String getPassword(String s) {
        User user = userService.getUser(s);
        if (user!=null) return user.getPassword();
        return null;
    }

    @Override
    protected Principal getPrincipal(String s) {
        return new GenericPrincipal(s, getPassword(s), Collections.singletonList("**"));
    }

    protected Principal getPrincipal(String userName,String password) {
        return new GenericPrincipal(userName, password, Collections.singletonList("**"));
    }

    @Override
    public Principal authenticate(String username, String credentials) {
        if (username!=null&&credentials!=null){
            String password = getPassword(username);
            if (StringUtils.hasText(password)){
                credentials = new Md5Hash(credentials, username, 1024).toHex();
                System.out.println(password.equals(credentials));
                if (credentials.equals(password)){
                    return this.getPrincipal(username,password);
                }
            }
        }
        return null;
    }
}
