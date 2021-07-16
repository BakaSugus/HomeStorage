package cn.j.netstorage.Config;

import cn.j.netstorage.Entity.Config;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Log.LogTemplate;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.Permission;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.oss.Oss;
import cn.j.netstorage.Service.*;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    private FileService2 fileService2;

    @Autowired
    private OriginFileService originFileService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        boolean device = createDevice();
        boolean permission = createPermission();
        boolean user = createAdminUser();
        boolean oss = createBackUpOss();
        //在设置驱动器创建独一无二的config.application 记录额外设置 包括但不限于邮件通知 自动转码
        boolean config = createConfig();
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

    private boolean createConfig() {

        Properties properties = null;
        try {
            HardDiskDevice device = hardDeviceService.get(Type.Setting);
            OriginFile originFile = originFileService.originFile("config.properties", device);
            if (originFile != null) return true;
            properties = new Properties();
            File file = device.get();
            file = new File(file, "config.properties");

            try (OutputStream output = new FileOutputStream(file);) {

                properties.setProperty("sendEmail", "");
                properties.setProperty("Emails", "");
                properties.setProperty("auto_encoding", "false");//视频转码
                properties.setProperty("size", "10");//空间大小限制
                properties.store(output, "" + new Date().toString());

                originFile = originFileService.originFile(file, device);
                originFileService.saveOriginFile(originFile);

                if (originFile == null || originFile.getOid() == 0) return false;
                Files files = fileService2.file("config.properties", originFile, "/设置/", config.getAdmin());
                return fileService2.save(files);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}
