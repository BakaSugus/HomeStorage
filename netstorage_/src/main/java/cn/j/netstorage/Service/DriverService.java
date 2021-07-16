package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.Driver.Driver;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.User.User;

import java.net.URL;
import java.util.List;

public interface DriverService {
    boolean putDriver(Driver driver);

    boolean upload(Driver driver, User user, long file_id);

    boolean upload(Driver driver, User user, Files files);

    List<String> getDriver(User user);

    List<Driver> getAllDriver();

    Driver getDriver(String bucketName, User user);

    boolean test(Driver driver);

    boolean delete(String bucketName, String[] key, User user);

    URL getDriverObjectUrl(Driver driver, String parentName, String selfName, User user);

    List<FilesDTO> Driver(String bucketName, User user, String path);

    boolean moveFiles(Driver driver, String key);

    boolean reName(Driver driver, String key);
}
