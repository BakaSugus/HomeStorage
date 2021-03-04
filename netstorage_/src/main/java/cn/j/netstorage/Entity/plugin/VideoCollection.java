package cn.j.netstorage.Entity.plugin;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Setter
@Getter
@ToString
@Entity(name = "t_VideoCollection")
@Table(name = "t_VideoCollection")
public class VideoCollection {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Column
    @OneToMany
    private Set<Files> files;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @Column
    private Date date;

    @Column
    private String spec;

}
