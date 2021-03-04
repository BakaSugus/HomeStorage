package cn.j.netstorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(exclude = JpaRepositoriesAutoConfiguration.class)
@EnableJpaRepositories(value = "cn.j.netstorage.Mapper")
@ComponentScan(value = "cn.j.netstorage.Controller")
@ComponentScan(value = "cn.j.netstorage.Config")


public class NetstorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(NetstorageApplication.class, args);
    }
}
