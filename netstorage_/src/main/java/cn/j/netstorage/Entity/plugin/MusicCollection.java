package cn.j.netstorage.Entity.plugin;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "t_MusicCollection")
@ToString
public class MusicCollection {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String collectionName;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Files> SongFiles;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    private User user;

    @Column
    private Date date;

}
