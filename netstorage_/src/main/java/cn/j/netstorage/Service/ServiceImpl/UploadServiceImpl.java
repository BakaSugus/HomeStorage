package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Folder;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.*;
import cn.j.netstorage.tool.FileData;
import cn.j.netstorage.tool.FilesUtil;
import cn.j.netstorage.tool.HashCodeUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

    @Override
    public Boolean common_upload(MultipartFile uploadFile, String storagePath, User user) {

        String fileName = uploadFile.getOriginalFilename();
        Type type = Type.getInstance(fileName.substring(fileName.lastIndexOf(".")));

        HardDiskDevice hardDiskDevice = hardDeviceService.get(type);

        String path = new File(hardDiskDevice.getFolderName() + "/" + uploadFile.getOriginalFilename()).getAbsolutePath();
        try {
            OriginFile originFile = originFileService.originFile(uploadFile, hardDiskDevice);

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


    @Override
    public Boolean slice_upload(MultipartFile file, int size, String fileName, String dst, String storagePath, int currentIndex, User user) {
        String tmpName = fileName + ".part" + currentIndex;
        try {
            File tempFolder=new File("/Temp");
            if (!tempFolder.exists())
                tempFolder.mkdirs();
            File part = new File(tempFolder.getAbsolutePath() + "/" + tmpName);
            if (!part.exists()) {
                file.transferTo(part);
            }
            if (size == currentIndex) {
                merge_upload(fileName, tempFolder.getAbsolutePath()+"/", storagePath, 1, size, user);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean exist_upload(String filePath, String storagePath, User user) {
        return null;
    }

    @Override
    public Boolean merge_upload(String fileName, String diskPath, String storagePath, int start, int end, User user) {
        Type type = Type.getInstance(fileName.substring(fileName.lastIndexOf(".")));
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
        try {
            originFile = originFileService.originFile(newFile, hardDiskDevice);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        originFile = originFileService.saveOriginFile(originFile);

        if (originFile.getOid() == 0) return false;
        //Files插入
        Folder folder = fileService2.getFolder(user, storagePath);
        if (folder != null) {
            user = folder.getOriginUser().iterator().next();
        }

        String finalName = this.getFinalFilesName(storagePath, fileName, type, user);

        Files files = fileService2.file(finalName, originFile, storagePath, user);
        afterProcess(files);
        fileService2.save(files);

        return files.getFid() != 0;
    }

//    @Override
//    public Boolean merge_upload(String fileName, String diskPath, String storagePath, int start, int end, User user) {
//        //OriginFile 插入
//        OriginFile originFile = null;
//        try {
//            originFile = originFileService.originFile(newFile, hardDiskDevice);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//        originFile = originFileService.saveOriginFile(originFile);
//
//        if (originFile.getOid() == 0) return false;
//        //Files插入
//        Folder folder = fileService2.getFolder(user, storagePath);
//        if (folder != null) {
//            user = folder.getOriginUser().iterator().next();
//        }
//
//        String finalName = this.getFinalFilesName(storagePath, fileName, type, user);
//
//        Files files = fileService2.file(finalName, originFile, storagePath, user);
//        afterProcess(files);
//        fileService2.save(files);
//
//        return files.getFid() != 0;
//    }

    @Override
    public Boolean checkMd5AndTransfer(String md5, String parentName, String selfName, User user) {
        Type type = Type.getInstance(selfName.substring(selfName.lastIndexOf(".")));
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
}
