package cn.j.netstorage.Entity.File;

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
@Table(name = "t_aria2")
@Entity
public class Aria2File {
    @Id
    @GeneratedValue
    private long id;

    @Column
    private String gid;//下载的任务id

    @Column
    private String name;

    @Column
    private String path;//她完成后应该存放的位置

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;//任务所属

    @Column
    private String type;


}
