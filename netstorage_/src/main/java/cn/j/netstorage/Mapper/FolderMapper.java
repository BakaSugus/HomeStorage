package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderMapper extends JpaRepository<Folder,Long> {

    Folder findByFolder(Files files);

    List<Folder> findByShareUser(User user);

    Folder findByShareUserAndFolderName(User user, String folderPath);

    List<Folder> findAllByOriginUser(User user);

    Folder findByOriginUserAndFolderName(User user,String folderName);

    List<Folder> findAllByShareUserAndInheritAndFiling(User user,boolean inherit,boolean filing);

    List<Folder> findAllByShareUserAndInheritAndShare(User user,boolean inherit,boolean Share);

    List<Folder> findAllByOriginUserAndFolderNameContaining(User user,String folderName);

    List<Folder> findAllByOriginUserAndFolder_ParentNameAndFolder_Visible(User user,String parentName,boolean visible);
}
