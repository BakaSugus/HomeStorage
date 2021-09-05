package cn.j.netstorage.Config;

import cn.j.netstorage.Entity.Config;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.FileService2;
import cn.j.netstorage.Service.FilesService;
import cn.j.netstorage.Service.HardDeviceService;
import cn.j.netstorage.Service.OriginFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class log {

    public static final String common_prefix = "/日志/";

    public static final String login_log_file = "login.log";

    public static final String init_log_file = "init.log";

    public static final String upload_log_file = "upload.log";

    public static final String error_log_file = "error.log";

    public static final String folder_log_file = "folder.log";

    public static final String generate_spec = "系统自动生成";

//    public log() {
//        this.log_table.put(login_log_file, getOrCreateAdminLogFile(login_log_file));
//        this.log_table.put(init_log_file, getOrCreateAdminLogFile(init_log_file));
//        this.log_table.put(upload_log_file, getOrCreateAdminLogFile(upload_log_file));
//        this.log_table.put(error_log_file, getOrCreateAdminLogFile(error_log_file));
//    }
}
