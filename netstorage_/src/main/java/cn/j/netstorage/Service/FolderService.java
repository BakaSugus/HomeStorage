package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.DTO.FolderDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.Folder.FolderPermission;

import cn.j.netstorage.Entity.User.User;

import java.util.List;
import java.util.Set;

public interface FolderService {

    //调整权限
    Boolean changePermission(Long folderId, Long[] id, User user, User originUser);

    //删除共享
    Boolean delete(Long id, User user);

    //检查权限
    Boolean checkPermission(String permission, User user);

    //提供共享文件夹
    List<FilesDTO> folders(User user);

    Folder folders(User user, String FolderName);

    Folder folders(Files files);

    List<FolderDTO> MyFolders(User user);

    List<FolderDTO> ShareToMe(User user);

    List<Folder> folders(Long... id);

    //提供权限选择
    Set<FolderPermission> permissions();

    Set<FolderPermission> permissions(String... name);

    Set<FolderPermission> permissions(Long... id);

    boolean shareFolder(Long fid, User user);

    boolean shareFolder(Long fid, User user, String... permission);

    boolean shareFolder(Long fid, Set<FolderPermission> permissions, User user);

    boolean shareFolder(Long fid, Long[] permissionId, User user);


}
