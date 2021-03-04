package cn.j.netstorage.Entity.User;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.shiro.crypto.hash.Md5Hash;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@ToString
@Table(name = "t_user",uniqueConstraints = {@UniqueConstraint(columnNames="email")})
public class User {

    @Id
    @GeneratedValue
    private long uid;
    @Column(name = "email", nullable = false)
    private String emailAccount;
    @Column(name = "nickName", nullable = false, length = 20)
    private String nickName;
    @Column(name = "password", nullable = false,length = 255)
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "role")
    private Set<Role> role;
    @Column(name = "createDate", nullable = false)
    private Long createDate;

    public void Md5Hash(){
        this.password=new Md5Hash(this.password, emailAccount, 1024).toHex();
    }
}
