package cn.j.netstorage.Entity.Folder;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.shiro.crypto.hash.Md5Hash;

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

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<User> shareUser;

    @Column
    private boolean filing;

    @Column
    private boolean share;

    @Column
    private boolean inherit;

    public String getParent(){
        String fullName = getFolderName();
        int count = fullName.lastIndexOf("/");
        return fullName.substring(0,count);
    }

}
