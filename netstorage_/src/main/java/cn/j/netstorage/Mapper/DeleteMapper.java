package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.File.DeleteFile;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeleteMapper extends JpaRepository<DeleteFile, Long> {
    List<DeleteFile> findAllByUserIs(User user);

    List<DeleteFile> findAllByUserIsAndIdIn(User user, Long... id);

}
