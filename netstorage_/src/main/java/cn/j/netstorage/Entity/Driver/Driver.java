package cn.j.netstorage.Entity.Driver;

import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@ToString
@Entity
@Table(name = "t_Driver")
public class Driver {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    private User user;

    @Column
    private String SecretId;

    @Column
    private String SecretKey;

    @Column
    private String Region;

    @Column
    private String bucketName;

    @Column
    private String type;

    @Column
    private String mapper;

    public final static String Ali = "Ali";
    public final static String OneDrive = "OneDrive";
    public final static String Cos = "Cos";
}
