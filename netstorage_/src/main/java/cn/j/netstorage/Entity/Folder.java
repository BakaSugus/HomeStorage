package cn.j.netstorage.Entity;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@ToString
@Entity
@Table(name = "t_folder")
public class Folder {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "files")
    private Set<Files> files;
    @Column
    private String folderPath;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    private Set<User> OriginUser;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "users")
    private List<User> users;

}
