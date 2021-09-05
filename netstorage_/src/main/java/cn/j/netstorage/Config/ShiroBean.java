package cn.j.netstorage.Config;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jitwxs
 * @date 2018/3/20 10:00
 */
@Configuration
public class ShiroBean {

    /**
     * 注入ShiroRealm
     * 不能省略，可能导致service无法注入
     */
    @Bean
    public ShiroRealm shiroRealm() {
        ShiroRealm shiroRealm = new ShiroRealm();
        shiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return shiroRealm;
    }

    /**
     * 密码校验规则HashedCredentialsMatcher
     * 这个类是为了对密码进行编码的 ,
     * 防止密码在数据库里明码保存 , 当然在登陆认证的时候 ,
     * 这个类也负责对form里输入的密码进行编码
     * 处理认证匹配处理器：如果自定义需要实现继承HashedCredentialsMatcher
     */
    @Bean("hashedCredentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        //指定加密方式为MD5
        credentialsMatcher.setHashAlgorithmName("MD5");
        //加密次数
        credentialsMatcher.setHashIterations(1024);
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        return credentialsMatcher;
    }

    /**
     * 注入securityManager
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(shiroRealm());
        manager.setRememberMeManager(rememberMeManager());
        return manager;
    }


    /**
     * Filter工厂，设置过滤条件与跳转条件
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();

        // Shiro的核心安全接口
        bean.setSecurityManager(securityManager);

        // 设置登陆页

        // 自定义拦截规则
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        //Map<String, String> map = new HashMap<>(16);
//        map.put("/#/storage", "authc");

        // 设置退出登陆
        map.put("/logout", "logout");
        // 对所有用户认证
        map.put("/User/login", "anon");

        map.put("/webdav/*","anon");

        bean.setFilterChainDefinitionMap(map);
        bean.setLoginUrl("/User/login");
        return bean;
    }


    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * 注册AuthorizationAttributeSourceAdvisor
     * 如果要开启注解，必须添加
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager manager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(manager);
        return advisor;
    }

    /**
     * cookie对象;
     * rememberMeCookie()方法是设置Cookie的生成模版，比如cookie的name，cookie的有效时间等等。
     *
     * @return
     */
    @Bean
    public SimpleCookie rememberMeCookie() {
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
        simpleCookie.setMaxAge(15*86400);
        return simpleCookie;
    }

    /**
     * cookie管理对象;
     * rememberMeManager()方法是生成rememberMe管理器，而且要将这个rememberMe管理器设置到securityManager中
     *
     * @return
     */
    @Bean
    public CookieRememberMeManager rememberMeManager() {
        //System.out.println("ShiroConfiguration.rememberMeManager()");
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        System.out.println(Base64.decode("2AvVhdsgUs0FSA3SDFAdag=="));
//        Cookie: Idea-ebda7b9c=8cece912-3788-4ac4-87b2-588378d62d95; JSESSIONID=C5D52D1C072A3EAAD3BCF5C4B2619829; rememberMe=bbmOL8J+/RZH1ZooqFGfhHzPEGUNhosbQT3x3UjIpCYvKrmaNysESjWEu0+i9sh7RWjNvkJME1+6S9pz/NzyDYUFf0ogsvNFEWHyueGhrnMj4j8kOxpZOZuIOmlBGORRVBW1m8S4WymrRvg9zxxuLUaVkFT9BvZ4NGcmFdG+CrBtgaFO/U07TY7qVVfgQ+jyCSfNs0oLMdZWDKPNnpsrc55haZeE79qUHSewWBk/x6Hnb1HCqO6PdZci9VYs0+IID2feGmeWHb7ES4EmfZi+gd0uVBPT8GCXFgZhtYvDwqgSeYnP+FbM9tXpEH/ZdjdkKmwWv2Cf5WB1JdHHqTwDFemP7A7ERziRjhoC0fj7I/tLy6XxKjEyIBE8LAKmppY2FJhRmYIlEskftUBsyeTeXI9jTZP39YBZqZ2PNVjOhCfM1LT+gVCzeBsPR/LDcURp+deDaAeDyBkEGSf8u/w0uGUIAXe70BKU0uO5PemJBCgCwqNd2Cz5bczCzl9GGiLexyJqIdi8Ub/NelftAv/BmateNIHVIpXrNo9x3thwOa8=
        cookieRememberMeManager.setCipherKey(Base64.decode("2AvVhdsgUs0FSA3SDFAdag=="));
        return cookieRememberMeManager;
    }
}
