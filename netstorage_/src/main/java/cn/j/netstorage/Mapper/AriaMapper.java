package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.File.Aria2File;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AriaMapper extends JpaRepository<Aria2File,Long> {

    List<Aria2File> findAllByUserIs(User user);

    Aria2File findByGid(String gid);

}
