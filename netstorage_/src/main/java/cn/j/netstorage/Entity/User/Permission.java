package cn.j.netstorage.Entity.User;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "t_permission")
public class Permission {
    @Id
    @GeneratedValue
    private long pid;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

}
