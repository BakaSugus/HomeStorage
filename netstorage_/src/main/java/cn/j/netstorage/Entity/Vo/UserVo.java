package cn.j.netstorage.Entity.Vo;

import cn.j.netstorage.Entity.User.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;


@Setter
@Getter
@ToString
public class UserVo {
    @javax.validation.constraints.Email
    private String Email;
    @Length(max = 20,min = 7)
    private String password;

    private String nickName;

    private String rid;

    private Boolean rememberMe;

}
