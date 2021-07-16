package cn.j.netstorage.Entity.File;

import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "t_Files")
@Setter
@Getter
@ToString
public class Files {

    @Id
    @GeneratedValue
    private long fid;

    @Column
    private String selfName;

    @Column
    private String parentName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "origin")
    private Set<OriginFile> originFile;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    private List<User> user;

    @Column
    private String Spec;

    @Column
    private String metadata;

    @Column
    private String pwd;

    @Column
    private Date createDate;

    @Column
    private String type;

    @Column
    private boolean visible;


    public static short is_dir = 1;
    public static short no_dir = 0;


    public static Files setFolder(String parentName, String selfName, User user) {
        Files files = new Files();
        files.setParentName(parentName);
        files.setSelfName(selfName);
        files.setType(Type.Folder.getType());
        files.setUser(Collections.singletonList(user));
        return files;
    }

    public static Files setMusicFile() {
        return new Files();
    }

    public static Files setCommonFile() {
        return new Files();
    }


    public OriginFile getOriginFile() {
        if (this.originFile == null || this.originFile.size() == 0) return null;
        return this.originFile.iterator().next();
    }

    public User getOneUser() {
        if (this.user == null || this.user.size() == 0) return null;

        return this.user.get(0);
    }

    public String getExt() {
        int count = this.selfName.lastIndexOf(".");
        if (count == -1)
            return "";
        return this.selfName.substring(count + 1);
    }

    public String getFullName() {
        return this.parentName + this.selfName;
    }

    public String getDiskFullName() {
        return getOriginFile().getPath();
    }

    public String getName() {
        int count = this.selfName.lastIndexOf(".");
        return this.selfName.substring(0,count);
    }
}
