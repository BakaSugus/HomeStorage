package cn.j.netstorage.Entity.File;

import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@ToString
@Table(name = "t_share")
@Entity
@Setter
@Getter
public class ShareFiles {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "files")
    private Files files;

    @Column
    private String token;

    @Column
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

}
