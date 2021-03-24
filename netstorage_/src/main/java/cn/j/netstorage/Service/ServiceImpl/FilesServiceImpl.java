package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.VisitRecord;
import cn.j.netstorage.Mapper.FileMapper;
import cn.j.netstorage.Mapper.FolderMapper;
import cn.j.netstorage.Mapper.RecordMapper;
import cn.j.netstorage.Service.FileService2;
import cn.j.netstorage.Service.FolderService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.FilesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FilesServiceImpl implements FileService2 {

    @Autowired
    FileMapper fileMapper;

    @Autowired
    UserService userService;

    @Autowired
    FolderMapper folderMapper;
    @Autowired
    FolderService folderService;


    @Override
    public Boolean save(Files files) {
        return fileMapper.save(files).getFid() != 0;
    }

    @Override
    public Files saveAndGet(Files files) {
        return fileMapper.save(files);
    }

    @Override
    public Boolean del(Files files) {
        try {
            fileMapper.deleteById(files.getFid());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean del(List<Files> files) {
        files.removeIf(file -> Type.Folder.getType().equals(file.getType()));
        try {
            fileMapper.deleteAll(files);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<Files> get(String path, String name, User user){
        return fileMapper.findAllBySelfNameAndUserAndParentName(name,user,path);
    }

    @Override
    public Boolean move(Files files, String path) {
        return null;
    }

    @Override
    public List<Files> files(Long... id) {
        return fileMapper.findAllById(Arrays.asList(id));
    }

    @Override
    public List<Files> files() {
        return null;
    }

    @Override
    public Files file(String finalName, OriginFile originFile, String storagePath, User user) {
        Files files = new Files();
        files.setParentName(storagePath);
        files.setUser(Collections.singletonList(user));
        files.setCreateDate(new Date());
        files.setOriginFile(Collections.singleton(originFile));
        files.setSelfName(finalName);

        Type type = Type.getInstance(FilesUtil.getExt(finalName));
        files.setType(type.getType());

        return files;
    }

    @Override
    public int checkFilesCount(String parentName, String fileName, User user) {
        int pos = fileName.lastIndexOf(".");
        if (pos == -1)
            pos = fileName.length();
        String likeName = fileName.substring(0, pos) + "%" + fileName.substring(pos);
        return fileMapper.findAllBySelfNameLikeAndUserAndParentName(likeName, user, parentName).size();
    }


    public List<FilesDTO> folders(User user) {
        List<FilesDTO> filesDto = new ArrayList<>();
        List<Folder> list = folderMapper.findByShareUser(user);
        for (Folder f :
                list) {
            filesDto.add(new FilesDTO(f.getFolder()));
        }
        return filesDto;
    }

    public Boolean deleteFolders(Long shareId) {
        try {
            folderMapper.deleteById(shareId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }



    @Override
    public Boolean RenameFile(User user, Long fid, String targetName) {
        List<Files> filesList = files(fid);
        Files files = null;
        if (filesList == null || filesList.size() < 1) {
            return false;
        }

        files = filesList.get(0);
        files.setSelfName(targetName);
        return save(files);
    }

    @Override
    public Boolean moveFiles(User user, Long fid, Long targetFid) {
        Files targetFiles = fileMapper.findById(targetFid).get();
        if (targetFiles == null || targetFiles.getType().equals(Type.Folder.getType())) {
            return false;
        }
        String parentName = targetFiles.getParentName() + targetFiles.getSelfName() + "/";
        Files file = fileMapper.findById(fid).get();
        file.setParentName(parentName);
        return save(file);
    }

    @Override
    public void zip(ZipOutputStream zipOutputStream, User user, Long... fid) {
        List<Files> files = files(fid);
        try {
            for (int i = 0; i < files.size(); i++) {
                Files f = files.get(i);
                if (f.getType().equals(Type.Folder)) {
                    //是文件夹
                    zipEntry(f.getParentName() + f.getSelfName() + "/", f, zipOutputStream);
                } else {
                    List<OriginFile> originFiles = new ArrayList<>(f.getOriginFile());
                    File file = new File(originFiles.get(originFiles.size() - 1).getPath());
                    FileInputStream fis = new FileInputStream(file);

                    zipOutputStream.putNextEntry(new ZipEntry(file.getName()));

                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zipOutputStream.write(bytes, 0, length);
                    }
                    fis.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    @Autowired
    RecordMapper recordMapper;

    @Override
    public boolean addVisitRecord(User user, Files files) {
        VisitRecord record = new VisitRecord();
        record.setFiles(files);
        record.setTime(System.currentTimeMillis());
        record.setType(files.getType());
        record.setUser(user);
        return recordMapper.save(record).getId() != 0;
    }

    @Override
    public Files getFiles(String path, String selfName, User user) {
        return fileMapper.findAllBySelfNameAndUserAndParentName(selfName,user,path).get(0);
    }

    public void zipEntry(String location, Files files, ZipOutputStream zipOutputStream) {
        //此处查是不是共享文件夹
        Folder folder = folderService.folders(files);
        User user = folder == null || folder.getOriginUser() == null ?
                files.getUser().get(0) : folder.getOriginUser();
        //此处查子文件和子文件夹
        List<Files> anySonFiles = fileMapper.findByParentNameLikeAndUser(files.getParentName() + files.getSelfName() + "/", user);
        for (int i = 0; i < anySonFiles.size(); i++) {
            Files f = anySonFiles.get(i);
            if (f.getType().equals(Type.Folder.getType())) {
                continue;
            }
            List<OriginFile> originFiles = new ArrayList<>(f.getOriginFile());
            try (
                    FileInputStream fis = new FileInputStream(new File(originFiles.get(originFiles.size() - 1).getPath()).getAbsolutePath());) {
                String name = f.getParentName() + f.getSelfName();
                ZipEntry zipEntry = new ZipEntry("/" + name.replace(location, ""));
                zipOutputStream.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOutputStream.write(bytes, 0, length);
                }
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
