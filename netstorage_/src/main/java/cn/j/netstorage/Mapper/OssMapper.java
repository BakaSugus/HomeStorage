package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.oss.Oss;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OssMapper extends JpaRepository<Oss,Long> {

    Oss findByUser(User user);

    List<Oss> findAllByUser(User user);

    List<Oss> findAllByUserAndBackupBucketNameIsNotNull(User user);
}
