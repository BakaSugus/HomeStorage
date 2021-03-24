package cn.j.netstorage.Entity.oss;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class OssFiles {
    private String path;
    private long size;
    private Date lastModifity;
    private String storageClasses;

    public OssFiles() {
    }

    public OssFiles(String path) {
        this.path=path;
    }

    public OssFiles(String path, long size, Date lastModifity, String storageClasses) {
        this.path = path;
        this.size = size;
        this.lastModifity = lastModifity;
        this.storageClasses = storageClasses;
    }
}
