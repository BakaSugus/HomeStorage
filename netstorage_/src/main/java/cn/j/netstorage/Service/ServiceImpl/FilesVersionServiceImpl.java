package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.FilesVersion;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.Operation;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Mapper.FilesVersionMapper;
import cn.j.netstorage.Service.FileService2;
import cn.j.netstorage.Service.FilesService;
import cn.j.netstorage.Service.FilesVersionService;
import cn.j.netstorage.Service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FilesVersionServiceImpl implements FilesVersionService {
    @Autowired
    private FileService2 fileService2;
    @Autowired
    private FilesService filesService;
    @Autowired
    private FolderService folderService;
    @Autowired
    FilesVersionMapper filesVersionMapper;

    @Override
    public boolean add(User upload, Files files) {
        if (files == null) return false;
        FilesVersion fileVersion = new FilesVersion();
        if (files.getUser().get(0) == upload) {
            fileVersion.setUser(upload);
            fileVersion.setUploadUser(upload);
        } else {
            fileVersion.setUser(files.getUser().get(0));
            fileVersion.setUploadUser(upload);
        }
        fileVersion.setUpdateDate(new Date());
        fileVersion.setFilesName(files.getSelfName());
        fileVersion.setParentName(files.getParentName());
        fileVersion.setOperation(Operation.Upload.getOperation());
        return filesVersionMapper.save(fileVersion).getId() != 0;
    }

    @Override
    public boolean delete(Files files, User user) {
        return false;
    }

    @Override
    public List<FilesVersion> all(User user) {
        return filesVersionMapper.findAllByUser(user);
    }

    @Override
    public List<FilesVersion> folder(User user, String path) {
        return filesVersionMapper.findAllByParentNameAndUser(path, user);
    }

    @Override
    public FilesVersion get(User user, Files files) {
        return filesVersionMapper.findByFilesNameAndParentNameAndUser(files.getSelfName(), files.getParentName(), user);
    }

    @Override
    public FilesVersion get(User user, Long fid) {
        Files files = new Files();
        files.setFid(fid);
        return get(user, files);
    }

    @Override
    public FilesVersion get(Long id) {
        return filesVersionMapper.getOne(id);
    }

    @Override
    public FilesVersion get(String path, String selfName, User user) {
        Files files = fileService2.getFiles(path, selfName, user);
        if (files == null)
            return null;

        if (files.getUser().get(0) == user)
            return get(user, files);

        Folder folder = folderService.folders(files);
        if (folder == null)
            return null;
        return null;
    }

    @Override
    public boolean saveLog(Files files, User user) {
        return false;
    }

    @Override
    public boolean saveLog(String parentName, String selfName, User user) {
        return false;
    }
}
