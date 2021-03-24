package cn.j.netstorage.Entity;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.File;

@Setter
@Getter
@ToString
@Table(name = "t_visitRecord")
@Entity
public class VisitRecord {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    private Files files;
    @Column
    private long Time;
    @ManyToOne
    private User user;
    @Column
    private String type;

}
