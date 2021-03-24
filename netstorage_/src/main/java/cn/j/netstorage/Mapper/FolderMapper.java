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

}
