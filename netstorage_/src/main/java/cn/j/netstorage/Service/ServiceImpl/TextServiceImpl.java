package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Config.log;
import cn.j.netstorage.Entity.Config;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Log.LogTemplate;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.*;
import cn.j.netstorage.tool.HashCodeUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Collections;

@Service
public class TextServiceImpl implements TextService {

    @Autowired
    private OriginFileService originFileService;

    @Autowired
    private FileService2 fileService2;

    @Autowired
    private FilesService filesService;

    @Autowired
    private Config config;

    @Autowired
    private HardDeviceService diskService;

    @Override
    public int checkOriginFileUse(Files files, User user) {
        OriginFile origin = files.getOriginFile();
        if (origin == null) return -1;

        return originFileService.count(origin.getPath());
    }

    @Override
    public boolean AppendLine(Files files, String content, User user) {

        if (content == null || StringUtils.isEmpty(content)) return false;

        if (files.getUser() == null || files.getUser().size() == 0) return false;

        int count = checkOriginFileUse(files, user);
        if (count == -1) return false;

        OriginFile origin = files.getOriginFile();
        boolean res=false;
        if (count > 1) {
            //新增文件修改原始文件
            File file = new File(origin.getPath());
            OriginFile copy = origin.copy();
            File dest = new File(copy.getPath());
            res = copy(file, dest, copy, files, content);
        } else {
            //原地修改 顺便修改md5值
            res = setText(content, origin.getPath(), true);
            return res;
        }
        return res;
    }

    @Override
    public boolean CoverContent(Files file, String content, User user) {
        if (file.getUser() == null || file.getUser().size() == 0) return false;
        int count = checkOriginFileUse(file, user);
        OriginFile origin = file.getOriginFile();

        if (count > 1) {
            //新增文件修改副本文件 将新文件指向file
            File originFile = new File(origin.getPath());
            OriginFile copy = origin.copy();
            File dest = new File(copy.getPath());
            return copy(originFile, dest, copy, file, content);
        } else {
            //原地修改 顺便修改md5值
            boolean res = setText(content, origin.getPath(), false);
            if (!res) return false;
            res = filesService.saveOriginFiles(origin);
            return res;
        }


    }

    public boolean copy(File file, File dest, OriginFile copy, Files diskfile, String content) {
        try {
            FileUtils.copyFile(file, dest);
            if (!dest.exists()) return false;

            boolean result = this.setText(content, dest.getAbsolutePath(), true);
            if (!result) return false;

            copy.setMd5(HashCodeUtil.getHashCode(dest));
            copy.setSize(dest.length());
            OriginFile value = originFileService.saveOriginFile(copy);
            if (!value.exist()) return false;

            diskfile.setOriginFile(Collections.singleton(value));
            return fileService2.save(diskfile);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     *
     * @deprecated this method has been move
     * @param content
     * @param path
     * @param append
     * @return
     */
    @Override
    public boolean setText(String content, String path, boolean append) {
        File file = new File(path);
        if (!file.exists())
            createEmptyFile(path);
        try (FileOutputStream fos = new FileOutputStream(file, append);
             OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");) {
            osw.write(content); //写入内容
            System.out.println(content);
            osw.write("\r\n");  //换行
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

//    @Override
//    public boolean setText(Files files, String content, boolean append) {
//        OriginFile originFile = files.getOriginFile();
//        File file = new File(originFile.getPath());
//        if (!file.exists())
//            createEmptyFile(path);
//        try (FileOutputStream fos = new FileOutputStream(file, append);
//             OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");) {
//            osw.write(content); //写入内容
//            osw.write("\r\n");  //换行
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return false;
//        }
//        return true;
//    }


    public OriginFile getOrCreateAdminLogOriginFiles(String name) {

        OriginFile originFile = null;
        HardDiskDevice device = diskService.get(Type.Setting);
        try {
            originFile = originFileService.originFile(name, device);
            if (originFile != null) return originFile;
            File file = new File(device.get(), name);
            originFile = originFileService.originFile(file, device);
            return originFileService.saveOriginFile(originFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Files getOrCreateAdminLogFile(String name) {
        if (config == null) return null;
        User user = config.getAdmin();
        if (user == null)
            return null;
        Files files = fileService2.getFiles(log.common_prefix, name, user);

        if (files != null) return files;

        OriginFile originFile = getOrCreateAdminLogOriginFiles(name);

        files = Files.setCommonFile(user, log.common_prefix, name, originFile, log.generate_spec);

        return fileService2.saveAndGet(files);
    }

    public Files GetOrCreateFolderLog(String prefix, String name, User user) {
        if (user == null) return null;

        Files files = fileService2.getFiles(prefix, name, user);
        if (files != null) return files;

        HardDiskDevice device = diskService.get(Type.Common);

        String logFile = System.currentTimeMillis() + ".log";
        File file = new File(device.get().getAbsolutePath(), logFile);
        try {
            OriginFile originFile = originFileService.originFile(file, device);
            originFileService.saveOriginFile(originFile);
            if (originFile == null) return null;
            files = Files.setCommonFile(user, prefix, name, originFile, log.generate_spec);
            return fileService2.saveAndGet(files);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean createEmptyFile(String path) {
        try {
            return new File(path).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
