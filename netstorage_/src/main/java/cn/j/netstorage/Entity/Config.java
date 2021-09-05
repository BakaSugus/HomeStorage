package cn.j.netstorage.Entity;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.oss.Oss;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.shiro.crypto.hash.Hash;

import java.io.File;
import java.util.HashMap;
import java.util.List;

@Setter
@Getter
@ToString
public class Config {

    private String filePath;

    private Oss oss;

    private User admin;

    private String aria;

    private HashMap<String, String> device_path;

    private String MySql;

    private EMail EMail;

    private Integer MaxSize;

    private boolean auto_convert;

    private HashMap<String,Files> log_table;

    public static final String MAXSIZE = "Size";

    public static final String EMAIl = "EMAIl";

    public static final String EMails = "EMails";

    public static final String ARIA2 = "ARIA2";

    public static final String DEVICE = "DEVICE";

    public static final String BACKUP_OSS = "BAKCUP";

    public static final String ADMIN = "ADMIN";
}
