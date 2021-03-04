package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.DTO.DeleteDTOs;
import cn.j.netstorage.Entity.Folder;
import cn.j.netstorage.Entity.User.User;

import java.util.List;

public interface DeleteService {

    DeleteDTOs deletes(User user);

    DeleteDTOs deletes(User user, Long... id);

    DeleteDTOs.DeleteDTO delete(Long id, User user);

    Boolean DeleteFolders(User user, Long... id);//删除文件夹

    Boolean restoreFolders(User user, Long... id);//还原这个文件夹

    Boolean restoreFiles(User user, Long... id);//还原文件

    Boolean DeleteFiles(User user, Long... id);//删除文件

    Boolean DeleteFilesInRecycleBin(User user,Long ... id);

    Boolean loopDeleteFiles();//遍历这些文件哪个到时间了 如果到时间了 而且没有被使用就被删除 如果是文件夹类型 遍历他的后代们

}
