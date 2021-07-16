package cn.j.netstorage.Config;

import cn.j.netstorage.Entity.Config;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.Log.LogTemplate;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.Usage;
import cn.j.netstorage.Entity.User.Permission;
import cn.j.netstorage.Entity.User.Role;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.oss.Oss;
import cn.j.netstorage.Mapper.PermissionMapper;
import cn.j.netstorage.Mapper.RoleMapper;
import cn.j.netstorage.Mapper.UsageMapper;
import cn.j.netstorage.Service.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Component
public class CheckEnv implements ApplicationRunner {

    @Autowired
    Config config;

    @Autowired
    private UserService userService;

    @Autowired
    private HardDeviceService hardDeviceService;

    @Autowired
    private OssService ossService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private UsageMapper usageMapper;

    @Autowired
    private FileService2 fileService2;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        boolean device = createDevice();
        boolean permission = createPermission();
        boolean user = createAdminUser();
        boolean oss = createBackUpOss();
    }

    private boolean createDevice() {
        return config != null && config.getDevice_path() != null && hardDeviceService.initDevice(config.getDevice_path());
    }

    public boolean createAdminUser() {

        User user = this.config.getAdmin();
        if (user == null || StringUtils.isEmpty(user.getEmailAccount())) {
            return false;
        }
        try {
            user = userService.getUser(this.config.getAdmin().getEmailAccount(), new Md5Hash(this.config.getAdmin().getPassword(), this.config.getAdmin().getEmailAccount(), 1024).toHex());
            if (user != null) {
                this.config.setAdmin(user);
                return true;
            }
            user = userService.createAdminUser(config.getAdmin());

            if (user == null || user.getUid() == 0) return false;
            Files files = fileService2.getFiles("/", "日志", user);
            boolean res = false;
            if (files == null || files.getFid() == 0) {
                res = uploadService.common_upload_Folder(Files.setFolder("/", "日志", user));
            }
            uploadService.uploadLog(user, "/日志/", "Start.log", LogTemplate.initLog(user, "管理员账户", "", res, null), false);

        } catch (Exception e) {
            uploadService.uploadLog(user, "/日志/", "Start.log", LogTemplate.initLog(user, "管理员账户", "", false, e.getMessage()), false);

        }


        this.config.setAdmin(user);
        return true;
    }

    public boolean createPermission() {
        String[] p = {"上传", "下载", "插件", "共享", "投屏", "外网访问", "bt下载", "打印", "离线下载"};
        try {
            List<Permission> permissions = userService.getAllPermission();
            if (permissions.size() >= p.length) return true;
            for (String s : p) {
                Permission permission = new Permission();
                permission.setName(s);
                userService.savePermission(permission);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public boolean createBackUpOss() {

        User user = this.config.getAdmin();
        if (user == null || user.getCreateDate() == 0) return false;
        Oss oss = ossService.get(this.config.getAdmin());

        if (oss == null || StringUtils.isEmpty(oss.getBackupBucketName())) return false;

        if (oss.getSecretId().equals(this.config.getOss().getSecretId()) && oss.getSecretKey().equals(this.config.getOss().getSecretKey())) {
            this.config.setOss(oss);
            return true;
        }

        return ossService.addBackup(this.config.getOss(), this.config.getAdmin());
    }

    public boolean createEnvData() {
        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress();
            Map<String, String> map = System.getenv();
            String userName = map.get("USERNAME");// 获取用户名
            String computerName = map.get("COMPUTERNAME");// 获取计算机名
            String userDomain = map.get("USERDOMAIN");// 获取计算机域名
            System.out.println("用户名:" + userName);
            System.out.println("计算机名:    " + computerName);
            System.out.println("计算机域名:    " + userDomain);
            System.out.println("本地ip地址:    " + ip);
            System.out.println("本地主机名:    " + addr.getHostName());
            System.out.println("创建时间：" + new Date().toLocaleString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean createUsage() {
        List<Usage> usages = usageMapper.findAll();
        if (usages == null || usages.size() == 0) return false;
        List<String> strings = new ArrayList<>();

        for (Usage usage : usages) {
            strings.add(usage.getName());
        }

        List<Usage> list = new LinkedList<>();

        for (Type type : Type.values()) {
            if (strings.contains(type.getType())) continue;
            Usage usage = new Usage();
            usage.setName(type.getType());
            usage.setSize(0L);
            list.add(usage);
        }

        try {
            usageMapper.saveAll(list);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
