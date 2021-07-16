package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.VisitRecord;

import java.util.List;
import java.util.zip.ZipOutputStream;

public interface FileService2 {

    Boolean save(Files files);

    Files saveAndGet(Files files);

    Boolean del(Files files);

    Boolean del(List<Files> files);

    List<Files> get(String path, String name, User user);

    Boolean move(Files files, String path);

    List<Files> files(Long... id);

    List<Files> files();

    Files file(String finalName, OriginFile originFile, String storagePath, User user);

    int checkFilesCount(String parentName, String fileName, User user);

//    //共享文件夹
//    boolean shareFolder(Long fid, Long[] permissionId, User user);

//    Folder getFolder(Files files);
//
//    List<FilesDTO> folders(User user);
//
//    Boolean deleteFolders(Long shareId);
//
//    Folder getFolder(Long id);
//
//    Folder getFolder(User user, String FolderName);

    Boolean RenameFile(User user, long fid, String targetName);

    Boolean moveFiles(User user, long fid, long targetFid);

    void zip(ZipOutputStream zipOutputStream, User user, Long... fid);

//    Boolean RenameFolder();

    boolean addVisitRecord(User user, Files files);

    Files getFiles(String path,String selfName,User user);

    boolean saveRecord(VisitRecord visitRecord);

    String checkName(String storagePath,String OriginalFilename,User user);

    List<String> getZipFileList(Files files);
}
