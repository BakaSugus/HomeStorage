package cn.j.netstorage.Entity;

import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_token",uniqueConstraints={@UniqueConstraint(columnNames={"user","token"})})
public class Token {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private Long user;
    @Column(nullable = false)
    private String token;

    public Token(String token) {
        this.token = token;
    }

    public Token(Long user) {
        this.user = user;
    }

    public Token(Long user, String token) {
        this.user = user;
        this.token = token;
    }
}
