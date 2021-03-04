package cn.j.netstorage.Entity.File;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.File;


@Setter
@Getter
@ToString
@Entity
@Table(name = "t_device")
public class HardDiskDevice {
    @Id
    @GeneratedValue
    Long id;
    @Column
    String DeviceName;
    @Column
    String folderName;//c:\\
    @Column
    String CustomName;//api/Files/
    @Column
    Long Size;
    @Column
    String rules;


    public File get(){
        return new File(folderName);
    }

}
