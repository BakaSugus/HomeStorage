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
    private Long id;

    @Column
    private String parentName;

    @ManyToOne
    private User user;

    @ManyToOne
    private User uploadUser;

    @Column
    private Date UpdateDate;

    @Column
    private String filesName;

    @Column
    private String operation;

}
