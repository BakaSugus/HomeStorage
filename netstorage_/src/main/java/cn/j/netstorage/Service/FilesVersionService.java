package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.FilesVersion;
import cn.j.netstorage.Entity.User.User;

import java.util.List;

public interface FilesVersionService {

    boolean add(User user,Files files);

    boolean delete(Files files,User user);

    List<FilesVersion> all(User user);

    List<FilesVersion> folder(User user, String path);

    FilesVersion get(User user,Files files);

    FilesVersion get(User user,Long fid);

    FilesVersion get(Long id);

    FilesVersion get(String path,String selfName,User user);

    boolean saveLog(Files files,User user);

    boolean saveLog(String parentName,String selfName,User user);

}
