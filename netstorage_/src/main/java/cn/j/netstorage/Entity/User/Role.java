package cn.j.netstorage.Entity.User;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Entity
@Setter
@Getter
@ToString
@Table(name = "t_role")
public class Role {

    @Id
    @GeneratedValue
    private long rid;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "permission")
    private Set<Permission> permission;

    @Column(name = "status")
    private Boolean status;

}
