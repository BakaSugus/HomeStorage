package cn.j.netstorage.Entity.Folder;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@Table(name = "t_folder_permission")
@Entity
@ToString
public class FolderPermission {
    @Id
    @GeneratedValue
    private long id;
    @Column
    private String permissionName;
    @Column
    private String spec;

}
