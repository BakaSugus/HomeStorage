package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.plugin.MusicCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicMapper extends JpaRepository<MusicCollection,Long> {

    List<MusicCollection> findAllByUserIs(User user);

}
