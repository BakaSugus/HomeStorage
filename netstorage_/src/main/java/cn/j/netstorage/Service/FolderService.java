package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.DTO.FolderDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.Folder.Folder;

import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.VisitRecord;

import java.util.List;

public interface FolderService {

    //删除共享
    Boolean delete(Long id, User user);

    //提供共享文件夹
    List<FilesDTO> folders(User user);

    Folder folders(User user, String FolderName);

    Folder folder(Long fid);

    Folder folders(Files files);

    Folder folderByOriginUser(User user, String folderName);

    List<FolderDTO> MyFolders(User user);

    List<FolderDTO> ShareToMe(User user);

    List<FilesDTO> ShareToMeFolders(User user);

    List<Folder> folders(Long... id);

    boolean shareFolder(Long fid, User user);

    boolean FilingFolder(Long fid, User user);

    boolean createFolder(Folder parent, Long fid, User OriginUser, User user);


    List<VisitRecord> getRecords(Folder folder, User user);

    List<VisitRecord> getRecords(Files files, User user);

    List<VisitRecord> getRecords(String parentName, String selfName, User user);

    List<VisitRecord> getRecords(String parentName, String selfName, String operation_type, User user);

    List<FolderDTO> Filing(User user);

    void changeFolderUsage(Folder folder, User user, Files files);

    List<FilesDTO> AllFolders(User user, String parentName,boolean visible);

    List<FilesDTO> MyShareFolders(User user, String parentName,boolean visible);

}
