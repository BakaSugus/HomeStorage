package cn.j.netstorage.Entity;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@ToString
@Setter
@Getter
@Table(name = "t_Record")
@Entity
public class Record {
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
