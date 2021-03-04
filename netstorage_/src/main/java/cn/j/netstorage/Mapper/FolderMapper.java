package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.Folder;
import cn.j.netstorage.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderMapper extends JpaRepository<Folder,Long> {

    Folder findByFiles(Files files);

    List<Folder> findByUsers(User user);

    Folder findByUsersAndFolderPath(User user, String folderPath);

}
