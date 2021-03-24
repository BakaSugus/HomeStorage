package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.Folder.Folder;
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
    public Boolean transferFiles(String file, String fileName, Long id, String diskName, User user) {
        File f = new File(file);

        if (!f.exists() || !fileName.equals(f.getName()))
            return false;

        Files files = fileService2.files(id).get(0);
        if (files == null || !Type.Folder.getType().equals(files.getType()))
            return false;
        //检查是不是共享文件夹 如果是共享文件夹就交由folder插入文件处理 如果不是就检测是不是统一的用户

        Folder folder = folderService.folders(user, files.getParentName());

        if (folder!=null){
            //todo folder插入文件处理 整合版本控制
        }else{
            if (!user.getEmailAccount().equals(files.getUser().get(0).getEmailAccount()))
                return false;
            //构造files 进行新增文件

//            String finalName=uploadService.getFinalFilesName(files.getSelfName());
        }
        return null;
    }

    @Override
    public Boolean transferFiles(List<File> files) {
        return null;
    }

    @Override
    public Boolean pyControl() {
        return null;
    }
}
