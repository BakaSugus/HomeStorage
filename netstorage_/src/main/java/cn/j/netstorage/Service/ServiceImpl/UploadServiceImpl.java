package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.Config;
import cn.j.netstorage.Entity.Driver.Driver;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.Log.Log;
import cn.j.netstorage.Entity.Log.LogTemplate;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.Token;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.VisitRecord;
import cn.j.netstorage.Entity.oss.Oss;
//import cn.j.netstorage.Mapper.StorageUsageMapper;
import cn.j.netstorage.Service.*;
import cn.j.netstorage.tool.FileData;
import cn.j.netstorage.tool.FilesUtil;
import cn.j.netstorage.tool.HashCodeUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class UploadServiceImpl implements UploadService {

    @Autowired
    private UserService userService;
    @Autowired
    private HardDeviceService hardDeviceService;

    @Autowired
    private OriginFileService originFileService;

    @Autowired
    private FileService2 fileService2;

    @Autowired
    private FilesService filesService;

    @Autowired
    private FolderService folderService;

    @Autowired
    private OssService ossService;

    @Autowired
    private TextService textService;
    @Autowired
    private DriverService driverService;
    @Autowired
    private Config config;

    @Override
    public Boolean common_upload(MultipartFile uploadFile, String storagePath, User user) {

        String fileName = uploadFile.getOriginalFilename();
        Type type = Type.getInstance(fileName);

        HardDiskDevice hardDiskDevice = hardDeviceService.get(type);
        String path = new File(hardDiskDevice.getFolderName() + "/" + uploadFile.getOriginalFilename()).getAbsolutePath();
        try {
            String ossKey = "/" + type.getType() + "/" + uploadFile.getName();
            OriginFile originFile = originFileService.originFile(uploadFile, hardDiskDevice);

            originFile.setOssKey(ossKey);
            uploadFile.transferTo(new File(path));
            //写原始文件
            originFile = originFileService.saveOriginFile(originFile);

            if (originFile.getOid() == 0) return false;

            String finalName = fileService2.checkName(storagePath, uploadFile.getOriginalFilename(), user);
            //如果folder 有自动上传这个属性就自动上传
            Files files = fileService2.file(finalName, originFile, storagePath, user);
            fileService2.save(files);

            Log upload = LogTemplate.UploadLog(user, files.getParentName() + files.getSelfName(), files.getFid() == 0, null);
            uploadLog(user, files.getParentName(), "", upload, false);
            Folder folder = folderService.folders(files.getUser().get(0), files.getParentName());


            backup(folder, user, files);

            if (files.getFid() == 0) return false;

        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean AutoUploadComplete(String token, String projectName, List<File> files) {
        return null;
    }

    @Override
    public Boolean AutoUploadComplete(String token, String projectName, File[] files) {
        return null;
    }

    @Override
    public Boolean AutoUploadComplete(String token, String projectName) {
        String path = getAutoUploadPath(token, projectName, true);
        if (path == null || StringUtils.isEmpty(path)) return false;
        File file = new File(path);
        if (!file.exists()) return false;
        User user = userService.getUser(token);
        dfs(file, String.format("/自动导入/%s/", projectName), user, projectName, 0);
        return true;
    }

    @Override
    public boolean uploadLog(User user, String filePath, String fileName, Log log, boolean visible) {
        //先找这个东西存不存在 不存在就干死他
        int count = fileService2.checkFilesCount(filePath, fileName, user);
        System.out.println("count:\t" + count);
        if (count == 0) {
            HardDiskDevice setting = hardDeviceService.get(Type.Setting);
            String diskName = System.currentTimeMillis() + ".log";
            File file = new File(setting.getFolderName(), diskName);
            try {
                boolean res = file.createNewFile();
                if (res) {
                    try (FileOutputStream fos = new FileOutputStream(file, true);
                         OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");) {
                        osw.write(log.toString()); //写入内容
                        osw.write("\r\n");  //换行
                    } catch (IOException ex) {
                        return false;
                    }
                    OriginFile originFile = originFileService.originFile(file, setting);

                    originFileService.saveOriginFile(originFile);

                    Files files = fileService2.file(fileName, originFile, filePath, user);
                    files.setVisible(visible);
                    return fileService2.save(files);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Files files = fileService2.getFiles(filePath, fileName, user);
            System.out.println(files);
            if (files == null) return false;
            OriginFile originFile = files.getOriginFile();
            boolean res = textService.setText(log.toString(), originFile.getPath(), true);
            if (res) {
                originFile.setSize(new File(originFile.getPath()).length());
                originFileService.saveOriginFile(originFile);
            } else {
                return false;
            }

        }
        return false;
    }

    //最初传进来的是文件夹 在网盘里的位置，
    private void dfs(File file, String path, User user, String projectName, int count) {
        File[] files = file.listFiles();
        if (files == null || files.length == 0) return;
        for (File f : files) {
            if (f.isDirectory()) {
                System.out.println(String.format("create Folder %s", f.getName()));
                common_upload_Folder(create(path, f.getName(), user, true));
                dfs(f, path + f.getName() + "/", user, projectName, count);
            } else {
                System.out.println(String.format("upload File %s", f.getName()));
                auto_upload(Type.AutoImport, "/自动导入", projectName, user, f);
                count++;
            }
        }
    }

    @Override
    public Boolean auto_upload(Type type, String folderName, String projectName, User user, File disk_file) {

        try {
            HardDiskDevice device = hardDeviceService.get(type);
            int count = originFileService.count(disk_file.getAbsolutePath());
            if (count == -1 || count > 0) return false;

            OriginFile originFile = new OriginFile();
            originFile.setFileName(disk_file.getName());
            originFile.setSize(disk_file.length());
            originFile.setMd5(HashCodeUtil.getHashCode(disk_file));
            originFile.setHardDiskDevice(Collections.singleton(device));
            originFileService.saveOriginFile(originFile);
            String storagePath;
            if (projectName != null)
                storagePath = String.format("%s/%s/", folderName, projectName);
            else
                storagePath = String.format("%s", folderName);

            Files files = fileService2.file(fileService2.checkName(storagePath, originFile.getFileName(), user), originFile, storagePath, user);
            fileService2.save(files);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String createAutoUploadPath(String token, String projectName, String desc) {
        String path = getAutoUploadPath(token, projectName, true);
        if (path == null || StringUtils.isEmpty(path)) return null;

        File file = new File(path);


        return null;
    }

    @Override
    public String getAutoUploadPath(String token, String projectName, boolean visible) {
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(projectName)) return null;

        User user = userService.getUser(new Token(token));

        Files files = fileService2.getFiles("/自动导入/", projectName, user);
        if (files == null) {
            common_upload_Folder(create("/自动导入/", projectName, user, visible));
        }
        HardDiskDevice device = hardDeviceService.get(Type.AutoImport);
        if (device == null) return null;

        String AutoImportPath = device.getFolderName();

        File file = new File(AutoImportPath);
        if (file.exists() || file.mkdirs()) {
            file = new File(file, projectName);
            if (file.exists() || file.mkdirs()) return file.getAbsolutePath();
        }
        return null;
    }


    @Override
    public Boolean common_upload_Folder(Files file) {
        if (file == null)
            return false;

        OriginFile files = filesService.insertFolder();//获得OriginFiles
        if (files.getOid() == 0) return false;

        int count = fileService2.checkFilesCount(file.getParentName(), file.getSelfName(), file.getUser().get(0));

        Folder parent = folderService.folderByOriginUser(file.getUser().get(0), file.getParentName());


        String finalName = String.format("%s%s",
                file.getSelfName(), count == 0 ? "" : "(" + count + ")");

        file.setSelfName(finalName);
        file.setType(Type.Folder.getType());
        file.setCreateDate(new Date());
        file.setOriginFile(Collections.singleton(files));
        file.setVisible(true);
        boolean res = fileService2.save(file);

        res = res && folderService.createFolder(parent, file.getFid(), file.getUser().get(0), file.getUser().get(0));

        User user = file.getUser().get(0);

        uploadLog(user, FilesUtil.append(file.getParentName(), file.getSelfName()) + "/", "folder.log", LogTemplate.initLog(user, file.getParentName() + file.getSelfName() + "文件夹", "", res, null), false);

        return res;
    }

    @Override
    public Boolean common_upload_Folder(String parentName, String selfName, User user) {
        Files files = new Files();
        return null;
    }

    @Override
    public Boolean slice_upload(MultipartFile file, int size, String fileName, String dst, String storagePath, int currentIndex, User user) {
        String tmpName = fileName + ".part" + currentIndex;

        try {
            HardDiskDevice tempDevice = hardDeviceService.get(Type.Temp);
            if (tempDevice == null) return false;
            File tempFolder = tempDevice.get();
            File part = new File(tempFolder.getAbsolutePath() + "/" + tmpName);
            System.out.println(part.getAbsolutePath());
            if (!part.exists()) {
                file.transferTo(part);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean exist_upload(String filePath, String selfName, String storagePath, User user) {
        if (!StringUtils.hasText(filePath) || !StringUtils.hasText(selfName) || !StringUtils.hasText(storagePath) || user == null)
            return false;
        File file = new File(filePath);
        if (!file.exists())
            return false;
        String fileName = file.getName();

        Type type = Type.getInstance(fileName);

        HardDiskDevice hardDiskDevice = hardDeviceService.get(type);

        File newFile = new File(hardDiskDevice.getFolderName() + "/" +
                +System.currentTimeMillis()
                + fileName.substring(fileName.lastIndexOf(".")));

        Boolean removeRes = file.renameTo(newFile);

        if (!removeRes)
            return false;

        OriginFile originFile = null;
        String ossKey = "/" + type.getType() + "/" + newFile.getName();
        try {
            originFile = originFileService.originFile(newFile, hardDiskDevice);
            originFile.setOssKey(ossKey);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        originFile = originFileService.saveOriginFile(originFile);

        if (originFile.getOid() == 0) return false;
        //Files插入
        Folder folder = folderService.folders(user, storagePath);

        User owner_user = null;
        //user是上传者 owner是拥有者, files里的user应该是拥有者 file_version里的应该是上传者和拥有者
        if (folder != null) {
            owner_user = folder.getOriginUser();
        }

        Files files = getFinalName(owner_user, user, storagePath, fileName, type, originFile);
        if (files == null) return false;
        afterProcess(files);
        files = fileService2.saveAndGet(files);


        return files.getFid() != 0;
    }

    @Override
    public Boolean merge_upload(String driver, String fileName, String diskPath, String storagePath, int start, int end, User user) {

        Type type = Type.getInstance(fileName);
        HardDiskDevice hardDiskDevice = hardDeviceService.get(type);
        File newFile = new File(hardDiskDevice.getFolderName() + "/" +
                +System.currentTimeMillis()
                + fileName.substring(fileName.lastIndexOf(".")));

        if (diskPath == null) {
            HardDiskDevice tempDevice = hardDeviceService.get(Type.Temp);
            if (tempDevice == null) return false;
            diskPath = tempDevice.get().getAbsolutePath() + "/";
        }

        if (start == end) {
            File file = new File(
                    diskPath + fileName + ".part" + start);
            if (file.exists()) {
                file.renameTo(newFile);
            }
        } else {
            try (FileOutputStream outputStream = new FileOutputStream(newFile, true);) {
                FileInputStream fileInputStream = null; //分片文件
                byte[] byt = new byte[2 * 1024 * 1024];
                int len;
                for (int i = start; i <= end; i++) {
                    fileInputStream = new FileInputStream(new File(
                            diskPath + fileName + ".part" + i)
                    );
                    while ((len = fileInputStream.read(byt)) != -1) {
                        outputStream.write(byt, 0, len);
                    }

                    fileInputStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            for (int i = start; i <= end; i++) {
                File file = new File(
                        diskPath + fileName + ".part" + i);
                if (file.exists()) {
                    file.delete();
                }
            }
        }

        //OriginFile 插入
        OriginFile originFile = null;
        String ossKey = "/" + type.getType() + "/" + newFile.getName();
        try {
            if (!newFile.exists()) return false;
            originFile = originFileService.originFile(newFile, hardDiskDevice);
            originFile.setOssKey(ossKey);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        originFile = originFileService.saveOriginFile(originFile);

        if (originFile.getOid() == 0) return false;
        Folder folder = folderService.folderByOriginUser(user, storagePath);
        User owner_user = null;
        if (folder != null) {
            owner_user = folder.getOriginUser();
        }

        Files files = getFinalName(owner_user, user, storagePath, fileName, type, originFile);
        switch (driver) {

            case "Default_Hidden":
                break;
            case "Default_Share":
                break;
            case "Default":
                break;
            default:
                return driverService.upload(driverService.getDriver(driver, user), user, files);
        }
        if (files == null) return false;
        afterProcess(files);
        files.setVisible(true);
        files = fileService2.saveAndGet(files);

        uploadLog(user, files.getParentName(), "Folder.log", LogTemplate.initLog(user, "上传了文件", "", files.getFid() != 0, null), false);

        backup(folder, user, files);
        folderService.changeFolderUsage(folder, user, files);
        return files.getFid() != 0;
    }

    public Files getFinalName(User owner_user, User user, String storagePath, String fileName, Type type, OriginFile originFile) {
        Files files = null;
        if (owner_user == user || owner_user == null) {
            String finalName = this.getFinalFilesName(storagePath, fileName, type, user);
            files = fileService2.file(finalName, originFile, storagePath, user);
        } else {
            String finalName = this.getFinalFilesName(storagePath, fileName, type, user);
            files = fileService2.file(finalName, originFile, storagePath, owner_user);
        }
        return files;
    }


    @Override
    public Boolean checkMd5AndTransfer(String md5, String parentName, String selfName, User user) {
        Type type = Type.getInstance(selfName);
        OriginFile originFile = originFileService.originFile(md5);
        if (originFile == null) {
            return false;
        }
        String finalName = this.getFinalFilesName(parentName, selfName, type, user);
        Files files = fileService2.file(finalName, originFile, parentName, user);
        return fileService2.save(files);
    }


    public void afterProcess(Files files) {
        if (Type.Common.getType().equals(files.getType())) {
            String metadata = FileData.CommonData(new File(files.getOriginFile().getPath()));
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("metadata", metadata);
            files.setMetadata(new Gson().toJson(jsonObject));
        }
    }

    public String getFinalFilesName(String folderName, String fileName, Type type, User user) {
        int count = fileService2.checkFilesCount(folderName, fileName, user);
        String finalName = null;
        if (type.getType().equals(Type.Folder.getType())) {
            finalName = String.format("%s%s",
                    fileName, count == 0 ? "" : "(" + count + ")");
        } else {
            finalName = String.format("%s%s%s",
                    fileName.substring(0, fileName.lastIndexOf(".")),
                    count == 0 ? "" : "(" + count + ")",
                    fileName.substring(fileName.lastIndexOf("."))
            );
        }
        return finalName;
    }

    @Override
    public Boolean multi_exist_upload(String taskName, String storagePath, String name, String filePath, User user) {
        if (user == null || !StringUtils.hasText(taskName) || StringUtils.hasText(storagePath) || StringUtils.hasText(filePath))
            return false;

        List<Files> files = fileService2.get(storagePath, taskName, user);
        if (files == null || files.size() == 0)
            common_upload_Folder(create(FilesUtil.append(storagePath, taskName), name, user, true));

        return exist_upload(filePath, name, FilesUtil.append(storagePath, taskName), user);
    }

    public Files create(String parent, String selfName, User user, boolean visible) {
        Files files = new Files();
        files.setType(Type.Folder.getType());
        files.setCreateDate(new Date());
        files.setParentName(parent);
        files.setSelfName(selfName);
        files.setVisible(visible);
        files.setUser(Collections.singletonList(user));
        return files;
    }

    public void backup(Folder folder, User user, Files files) {
        if (folder == null || user == null || files == null) return;
        if (folder.isFiling()) {
            VisitRecord backup = null;
            User admin = config.getAdmin();
            if (admin == null) return;
            List<Oss> oss = ossService.getBackUpOss(admin);
            if (oss != null) {
                for (Oss o : oss) {
                    files.setParentName("/" + user.getNickName() + files.getParentName());
                    boolean backup_res = ossService.upload(config.getAdmin(), o.getBackupBucketName(), files);
                    backup = VisitRecord.setFiles(files, user, VisitRecord.BACKUP, files.getFid() == 0, String.format("%s %s%s自动上传 结果: %s", DateFormat.getDateTimeInstance(), files.getParentName(), files.getSelfName(), backup_res ? "成功" : "失败"), "");
                }
            } else {
                backup = VisitRecord.setFiles(files, user, VisitRecord.BACKUP, files.getFid() == 0, String.format("%s %s%s自动上传失败", DateFormat.getDateTimeInstance(), files.getParentName(), files.getSelfName()), "不存在备份Oss,请检查");
            }
            fileService2.saveRecord(backup);
        }
    }

}
