package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.Driver.Driver;
import cn.j.netstorage.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverMapper extends JpaRepository<Driver,Long> {

    Driver getByBucketNameAndUser(String bucketName,User user);

    List<Driver> findAllByUser(User user);

}
