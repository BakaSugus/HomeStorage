package cn.j.netstorage.Entity.File;

import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@ToString
@Table(name = "t_FilesVersion")
@Entity
public class FilesVersion {
    @Id
    @GeneratedValue
    private Long GroupId;
    @Column
    private String GroupName;
    @Column
    private double version;
    @Column
    private String Desc_;
    @Column
    private Date UpdateDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "originFile")
    private Set<OriginFile> originFileSet;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    private Set<User> users;

}
