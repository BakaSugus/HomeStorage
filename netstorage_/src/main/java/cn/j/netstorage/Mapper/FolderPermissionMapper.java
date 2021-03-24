package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.Folder.FolderPermission;
import cn.j.netstorage.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface FolderPermissionMapper extends JpaRepository<FolderPermission,Long>{
    Set<FolderPermission> findByPermissionNameIn(String [] name);
}
