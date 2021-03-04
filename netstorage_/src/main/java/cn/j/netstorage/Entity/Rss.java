package cn.j.netstorage.Entity;

import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Table(name = "t_Rss")
@ToString
@Entity
public class Rss {
    @Id
    @GeneratedValue
    private long id;
    @Column
    private String name;
    @Column
    private String cron;
    @Column
    private String url;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    private User user;
}
