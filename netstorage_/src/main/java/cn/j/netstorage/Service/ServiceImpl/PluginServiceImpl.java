package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.Log.Log;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class PluginServiceImpl implements PluginService {

    @Autowired
    private FilesService filesService;

    @Autowired
    private FileService2 fileService2;

    @Autowired
    private UploadService uploadService;

    @Autowired
    FolderService folderService;


    @Override
    public boolean Mission(Files files, String cmd) {
        return false;
    }

    @Override
    public boolean AriaPlugin() {
        return false;
    }

    @Override
    public boolean FFmpegPlugin() {
        return false;
    }

    @Override
    public boolean checkPlugin() {
        return false;
    }

    @Override
    public boolean getPlugins() {
        return false;
    }

    @Override
    public boolean setPluginLog(String PluginName) {
        return false;
    }

    @Override
    public boolean Env() {
        return false;
    }

    @Override
    public boolean createLog(Folder folder, Log log) {
        return false;
    }
}
