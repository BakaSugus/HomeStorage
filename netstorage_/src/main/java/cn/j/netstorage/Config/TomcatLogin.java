package cn.j.netstorage.Config;

import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.UserService;
import org.apache.catalina.CredentialHandler;

import org.apache.catalina.Realm;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.authenticator.BasicAuthenticator;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.MessageDigestCredentialHandler;
import org.apache.catalina.realm.RealmBase;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.security.Principal;
import java.util.Collections;

@Configuration
public class TomcatLogin implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    @Autowired
    private TomcatRealm tomcatRealm;

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        TomcatServletWebServerFactory tomcatServletWebServerFactory = (TomcatServletWebServerFactory) factory;

        tomcatServletWebServerFactory.addContextCustomizers(context -> {
            context.setRealm(tomcatRealm);

            AuthenticatorBase digestAuthenticator = new BasicAuthenticator();
            SecurityConstraint securityConstraint = new SecurityConstraint();
            securityConstraint.setAuthConstraint(true);
            securityConstraint.addAuthRole("**");
            SecurityCollection collection = new SecurityCollection();
            collection.addPattern("/webdav/*");
            securityConstraint.addCollection(collection);
            context.addConstraint(securityConstraint);
            context.getPipeline().addValve(digestAuthenticator);
        });
    }
}