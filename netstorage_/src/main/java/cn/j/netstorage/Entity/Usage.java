package cn.j.netstorage.Entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@ToString
@Entity
@Table(name = "t_usage")
public class Usage {
    @Id
    @GeneratedValue
    private long id;

    @Column
    private String Name;

    @Column
    private long size;

}
