package cn.j.netstorage.Entity;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.oss.Oss;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.File;
import java.text.DateFormat;

@Setter
@Getter
@ToString
@Table(name = "t_visitRecord")
@Entity
public class VisitRecord {
    @Id
    @GeneratedValue
    private long id;

    @Column
    private String parentFolder;

    @Column
    private String selfName;

    @Column
    private long Time;

    @ManyToOne
    private User user;

    @Column
    private String operationType;

    @Column
    private boolean isSuccess;

    @Column
    private String spec;

    @Column
    private String reason;

    @Column
    private String file_type;

    public static final String UPLOAD = "UPLOAD";

    public static final String DELETE = "DELETE";

    public static final String OPEN = "OPEN";

    public static final String BACKUP = "BACKUP";


    public static VisitRecord setFiles(Files files, User user, String type, boolean isSuccess, String spec, String reason) {
        VisitRecord record=new VisitRecord();
        if (files != null && StringUtils.hasText(type) && user != null) {
            record.Time = System.currentTimeMillis();
            record.selfName = files.getSelfName();
            record.parentFolder = files.getParentName();
            record.user = user;
            record.operationType = type;
            record.file_type = files.getType();
            record.spec = spec;
            record.reason = reason;
            record.isSuccess = isSuccess;
            return record;
        }
        return null;
    }
}
