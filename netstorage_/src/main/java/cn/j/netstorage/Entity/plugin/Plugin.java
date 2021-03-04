package cn.j.netstorage.Entity.plugin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@ToString
@Getter
@Setter
@Entity
@Table(name = "t_Plugin")
public class Plugin {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String ip;
    @Column
    private String port;
    @Column
    private String mapping;
    @Column
    private String pluginName;
    @Column
    private Boolean status;

}
