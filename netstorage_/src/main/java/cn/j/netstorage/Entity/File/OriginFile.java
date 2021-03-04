package cn.j.netstorage.Entity.File;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "t_originFile")
public class OriginFile {
    @Id
    @GeneratedValue
    private long oid;
    @Column
    private String md5;
    @Column
    private String fileName;
    @Column
    private Long size;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "hardDiskDevice")
    private Set<HardDiskDevice> hardDiskDevice;

    public String getPath(){
        return new ArrayList<>(this.hardDiskDevice).get(0).getFolderName()+"/"+this.fileName;
    }

    public String getCustomPath(){
        return new ArrayList<>(this.hardDiskDevice).get(0).getCustomName()+"/"+this.fileName;
    }
}
