package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.oss.Oss;
import cn.j.netstorage.Entity.oss.OssFiles;

import java.util.List;

public interface OssService {


    boolean upload(User user, String path, Files files);

    boolean upload(User user, String path, Long id);

    boolean download(User user,String path, OriginFile originFile);

    boolean delete(User user,String path, OriginFile originFile);

    boolean add(Oss oss,User user);

    Oss get(User user);

    List<OssFiles> get(Oss oss,String name,String prefix);

    List<OssFiles> get(User user,String name,String prefix);

    List<OssFiles> getAllBucket(User user);

    boolean backup(User user,String bucketName,Files files);

    boolean createBucket(User user,String bucketName);

}
