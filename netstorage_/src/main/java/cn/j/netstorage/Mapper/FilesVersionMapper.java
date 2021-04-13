package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.DTO.FilesVersionDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.FilesVersion;
import cn.j.netstorage.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilesVersionMapper extends JpaRepository<FilesVersion, Long> {

    List<FilesVersion> findAllByParentNameAndUser(String path,User user);
    List<FilesVersion> findAllByUser(User user);
    FilesVersion findByFilesNameAndParentNameAndUser(String filesName,String parentName,User user);
}
