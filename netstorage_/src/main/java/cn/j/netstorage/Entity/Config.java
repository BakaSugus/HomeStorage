package cn.j.netstorage.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Config {
    private String Ip;
    private String RootPath;
    private String User;
    private String Password;

}
