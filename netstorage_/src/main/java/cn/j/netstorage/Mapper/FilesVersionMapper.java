package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.DTO.FilesVersionDTO;
import cn.j.netstorage.Entity.File.FilesVersion;
import cn.j.netstorage.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilesVersionMapper extends JpaRepository<FilesVersion, Long> {

    List<FilesVersion> findAllByUsers(User user);

}
