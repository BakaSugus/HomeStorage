package cn.j.netstorage.Entity.User;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Setter
@Getter
@Table(name = "t_token")
@Entity
public class Token {

    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String token;
    @OneToOne
    private User user;
    public Token(){}

    public Token(User user) {
        this.user = user;
        this.token = UUID.randomUUID().toString().replace("-", "");
    }

    public Token(String token){
        this.token=token;
    }
}
