package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.*;
import cn.j.netstorage.tool.FileData;
import cn.j.netstorage.tool.FilesUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.thymeleaf.util.StringUtils.append;

@Service
public class UploadServiceImpl implements UploadService {

    @Autowired
    HardDeviceService hardDeviceService;

    @Autowired
    OriginFileService originFileService;

    @Autowired
    FileService2 fileService2;

    @Autowired
    FilesService filesService;

    @Autowired
    FolderService folderService;


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
            //写用户文件
            int count = fileService2.checkFilesCount(storagePath, uploadFile.getOriginalFilename(), user);
            String finalName = String.format("%s%s%s",
                    uploadFile.getOriginalFilename().substring(0, uploadFile.getOriginalFilename().lastIndexOf(".")),
                    count == 0 ? "" : "(" + count + ")",
                    uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf("."))
            );

            Files files = fileService2.file(finalName, originFile, storagePath, user);
            fileService2.save(files);

            if (files.getFid() == 0) return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean common_upload_Folder(Files file) {
        if (file == null)
            return false;

        OriginFile files = filesService.insertFolder();//获得OriginFiles
        if (files.getOid() == 0) return false;

        int count = fileService2.checkFilesCount(file.getParentName(), file.getSelfName(), file.getUser().get(0));
        String finalName = String.format("%s%s",
                file.getSelfName(), count == 0 ? "" : "(" + count + ")");
        file.setSelfName(finalName);
        file.setType(Type.Folder.getType());
        file.setCreateDate(new Date());
        file.setOriginFile(Collections.singleton(files));
        return fileService2.save(file);
    }

    @Value("${workSpace}")
    private String workSpace;
    @Override
    public Boolean slice_upload(MultipartFile file, int size, String fileName, String dst, String storagePath, int currentIndex, User user) {
        String tmpName = fileName + ".part" + currentIndex;
        try {
            File tempFolder = new File(workSpace+"/Temp");
            if (!tempFolder.exists())
                tempFolder.mkdirs();
            File part = new File(tempFolder.getAbsolutePath() + "/" + tmpName);
            if (!part.exists()) {
                file.transferTo(part);
            }
            if (size == currentIndex) {
                merge_upload(fileName, tempFolder.getAbsolutePath() + "/", storagePath, 1, size, user);
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
//        System.out.println("transfer result:"+removeRes);
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
//        System.out.println("files:"+files);
        if (files == null) return false;
        afterProcess(files);
        files = fileService2.saveAndGet(files);
        if (files.getFid() != 0) filesVersionService.add(user, files);
        return files.getFid() != 0;
    }

    @Autowired
    FilesVersionService filesVersionService;

    @Override
    public Boolean merge_upload(String fileName, String diskPath, String storagePath, int start, int end, User user) {

        Type type = Type.getInstance(fileName);
        HardDiskDevice hardDiskDevice = hardDeviceService.get(type);
        File newFile = new File(hardDiskDevice.getFolderName() + "/" +
                +System.currentTimeMillis()
                + fileName.substring(fileName.lastIndexOf(".")));
        if (start == end) {
            File file = new File(
                    diskPath + fileName + ".part" + start);
            if (file.exists()) {
                file.renameTo(newFile);
            }
        } else {
            try (FileOutputStream outputStream = new FileOutputStream(newFile, true);) {
                FileInputStream fileInputStream = null; //分片文件
                byte[] byt = new byte[10 * 1024 * 1024];
                int len;
                for (int i = start; i < end; i++) {
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
        //加入文件上传记录
        if (files.getFid() != 0) filesVersionService.add(user, files);
        //是否备份
        return files.getFid() != 0;
    }

    private Files getFinalName(User owner_user, User user, String storagePath, String fileName, Type type, OriginFile originFile) {
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
        //插入该用户的文件夹
        String finalName = this.getFinalFilesName(parentName, selfName, type, user);
        Files files = fileService2.file(finalName, originFile, parentName, user);
        return fileService2.save(files);
    }


    public void afterProcess(Files files) {
        if (Type.Common.getType().equals(files.getType())) {
            String metadata = FileData.CommonData(new File(files.getOriginFile().iterator().next().getPath()));
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
            common_upload_Folder(create(FilesUtil.append(storagePath, taskName), name, user));

        return exist_upload(filePath, name, FilesUtil.append(storagePath, taskName), user);
    }

    public Files create(String parent, String selfName, User user) {
        Files files = new Files();
        files.setType(Type.Folder.getType());
        files.setCreateDate(new Date());
        files.setParentName(parent);
        files.setSelfName(selfName);
        files.setUser(Collections.singletonList(user));
        return files;
    }


}
