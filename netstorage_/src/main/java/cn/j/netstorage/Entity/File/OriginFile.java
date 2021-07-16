package cn.j.netstorage.Entity.File;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.File;
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

    @Column
    private String ossKey;

    public String getPath() {
        System.out.println(this.hardDiskDevice.size());
        return new ArrayList<>(this.hardDiskDevice).get(0).getFolderName() + "/" + this.fileName;
    }

    public String getCustomPath() {
        return new ArrayList<>(this.hardDiskDevice).get(0).getCustomName() + "/" + this.fileName;
    }

    public String getName(){
        if (this.fileName==null|| StringUtils.isEmpty(fileName))
            return null;
        int index = checkPointIndex(fileName);
        if (index==-1)return null;
        return fileName.substring(0,index);
    }

    public String getExt(){
        if (this.fileName==null|| StringUtils.isEmpty(fileName))
            return null;
        int index = checkPointIndex(fileName);
        if (index==-1)return null;
        return fileName.substring(index+1,fileName.length());
    }

    public String createTimeName(String ext){
        if (ext==null)
            return System.currentTimeMillis()+"";
        return System.currentTimeMillis()+"."+ext;
    }


    public int checkPointIndex(String fileName){
        return fileName.lastIndexOf(".");
    }

    public OriginFile copy(){
        OriginFile file = new OriginFile();

        file.setFileName(createTimeName(getExt()));
        file.setHardDiskDevice(this.hardDiskDevice);
        file.setOssKey(this.ossKey);
        return file;
    }

    public boolean exist() {
        String path = getPath();
        return path != null && new File(path).exists();
    }
}
