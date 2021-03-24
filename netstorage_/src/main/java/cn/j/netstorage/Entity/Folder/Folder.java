package cn.j.netstorage.Entity.Folder;

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

    @ManyToOne
    private Files folder;

    @Column
    private String folderName;

    @ManyToOne
    private User originUser;

    @ManyToOne
    private User shareUser;

    @ManyToMany
    private Set<FolderPermission> permissions;

}
