package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.plugin.VideoCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoMapper extends JpaRepository<VideoCollection,Long>{

    List<VideoCollection> findAllByUserIs(User user);
}
