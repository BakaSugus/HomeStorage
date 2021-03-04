package cn.j.netstorage.Entity.File;

import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.tool.FilesUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;

@Entity
@Table(name = "t_delete")
@Setter
@Getter
@ToString
public class DeleteFile {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    private User user;

    @Column
    private String originDiskParentName;

    @Column
    private String originDiskFileName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "originFile")
    private OriginFile originFile;

    @Column
    private Long size;

    @Column
    private String type;

    @Column
    private Long date;

    public DeleteFile(){}

    public DeleteFile(Files files) {
        if (files.getOriginFile()!=null&&files.getOriginFile().size()!=0){
            OriginFile originFile=files.getOriginFile().iterator().next();
            this.user = files.getUser().get(0);
            this.date = System.currentTimeMillis();
            this.originDiskFileName = files.getSelfName();
            this.originDiskParentName = files.getParentName();
            this.originFile = originFile;
            this.type = files.getType();
            this.size=originFile.getSize();
        }
    }

    public Files toFiles(){
        Files files=new Files();
        files.setParentName(this.originDiskParentName);
        files.setSelfName(this.originDiskFileName);
        files.setOriginFile(Collections.singleton(originFile));
        files.setType(this.type);
        files.setUser(Collections.singletonList(this.user));
        files.setCreateDate(new Date());
        return files;
    }

}
