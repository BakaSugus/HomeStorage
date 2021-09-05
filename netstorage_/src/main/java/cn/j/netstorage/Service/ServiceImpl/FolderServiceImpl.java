package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.DTO.FolderDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.VisitRecord;
import cn.j.netstorage.Mapper.FolderMapper;
import cn.j.netstorage.Mapper.RecordMapper;
import cn.j.netstorage.Service.FileService2;
import cn.j.netstorage.Service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FolderServiceImpl implements FolderService {

    @Autowired
    private FolderMapper folderMapper;
    @Autowired
    private FileService2 fileService2;

    @Autowired
    private RecordMapper recordMapper;

    public Folder getFolder(Files files) {
        return folderMapper.findByFolder(files);
    }

    public List<FilesDTO> folders(User user) {
        List<FilesDTO> filesDto = new ArrayList<>();
        List<Folder> list = folderMapper.findByShareUser(user);
        for (Folder f : list) {
            filesDto.add(new FilesDTO(f.getFolder()));
        }
        return filesDto;
    }

    @Override
    public Boolean delete(Long id, User user) {
        return null;
    }

    @Override
    public List<Folder> folders(Long... id) {
        return this.folderMapper.findAllById(Arrays.asList(id));
    }

    @Override
    public Folder folders(User user, String FolderName) {
        return folderMapper.findByShareUserAndFolderName(user, FolderName);
    }

    @Override
    public Folder folder(Long fid) {
        Files file = new Files();
        file.setFid(fid);
        return folders(file);
    }

    @Override
    public Folder folders(Files files) {
        if (files == null) return null;
        return folderMapper.findByFolder(files);
    }

    @Override
    public Folder folderByOriginUser(User user, String folderName) {
        return folderMapper.findByOriginUserAndFolderName(user, folderName);
    }

    @Override
    public List<FolderDTO> MyFolders(User user) {
        if (user == null) return null;
        List<FolderDTO> list = new ArrayList<>();
        List<Folder> folders = folderMapper.findAllByOriginUser(user);
        folders.forEach(value -> list.add(new FolderDTO(value)));
        return list;
    }

    @Override
    public List<FolderDTO> ShareToMe(User user) {
        if (user == null) return null;
        //查找不是继承来的共享文件夹
        List<Folder> folders = folderMapper.findAllByShareUserAndInheritAndShare(user, false, true);
        List<FolderDTO> res = new ArrayList<>();
        for (Folder folder : folders) {
            res.add(new FolderDTO(folder));
        }
        return res;
    }

    @Override
    public List<FilesDTO> ShareToMeFolders(User user) {
        List<FilesDTO> file = new ArrayList<>();
        List<Folder> folders = folderMapper.findAllByShareUserAndInheritAndShare(user, false, true);
        for (Folder folder : folders) {
            file.add(new FilesDTO(folder));
        }
        return file;
    }

    @Override
    public boolean FilingFolder(Long fid, User user) {
        List<Files> files = fileService2.files(fid);
        if (files == null || files.size() < 1) {
            return false;
        }

        Files file = files.get(0);
        if (!file.getType().equals(Type.Folder.getType())) return false;

        Folder folder = folders(file);
        if (folder == null || folder.getOriginUser().getEmailAccount().equals(user.getEmailAccount())) return false;

        folder.setInherit(false);
        folder.setFiling(true);
        return save(folder);
    }

    @Override
    public boolean shareFolder(Long fid, User user) {
        //todo 根据fid获得Files 根据Files获得Folder 改写是否共享的属性,设置为非继承来的属性 然后重新保存
        List<Files> files = fileService2.files(fid);
        if (files == null || files.size() < 1) {
            return false;
        }

        Files file = files.get(0);
        if (!file.getType().equals(Type.Folder.getType())) return false;

        Folder folder = folders(file);
        if (folder == null) return false;

        Set<User> users = folder.getShareUser();
        users.add(user);
        folder.setShareUser(users);
        folder.setOriginUser(file.getUser().get(0));
        folder.setShare(true);
        folder.setInherit(false);

        return save(folder);
    }

    @Override
    public boolean createFolder(Folder parent, Long fid, User OriginUser, User user) {
        return createFolder(parent, fid, user);
    }

    @Override
    public List<VisitRecord> getRecords(Folder folder, User user) {
        return recordMapper.getAllByParentFolder(folder.getFolderName());
    }

    @Override
    public List<VisitRecord> getRecords(Files files, User user) {
        return recordMapper.getAllByParentFolderAndSelfName(files.getParentName(), files.getSelfName());
    }

    @Override
    public List<VisitRecord> getRecords(String parentName, String selfName, User user) {
        return recordMapper.getAllByParentFolderAndSelfName(parentName, selfName);
    }

    @Override
    public List<VisitRecord> getRecords(String parentName, String selfName, String TYPE, User user) {
        return recordMapper.getAllByParentFolderAndSelfNameAndOperationType(parentName, selfName, TYPE);
    }

    @Override
    public List<FolderDTO> Filing(User user) {
        List<Folder> folders = folderMapper.findAllByShareUserAndInheritAndFiling(user, false, true);
        List<FolderDTO> res = new ArrayList<>();
        for (Folder folder : folders) {
            res.add(new FolderDTO(folder));
        }
        return res;
    }

    public boolean createFolder(Folder parent, Long fid, User user) {
        //创建文件夹函数 默认继承父文件夹的属性 默认继承父文件夹共享的用户
        Files files = fileService2.files(fid).get(0);
        System.out.println("FILES：" + files);
        if (files == null) {
            return false;
        }

        if (!files.getType().equals(Type.Folder.getType())) {
            return false;
        }

        Folder folder = getFolder(files);

        if (folder == null) {
            folder = new Folder();
            folder.setShareUser(new HashSet<>());
        } else {
            return false;
        }

        folder.setFolder(files);
        folder.setFolderName(files.getParentName() + files.getSelfName() + "/");
        folder.setOriginUser(files.getUser().get(0));

        folder.setFiling(false);
        folder.setShare(false);
        folder.setInherit(false);
        System.out.println("folder:" + folder);
        System.out.println("parent:" + parent);
        if (parent != null) {
            Set<User> users = new HashSet<>(parent.getShareUser());
            folder.setShareUser(users);
            folder.setFiling(parent.isFiling());
            folder.setShare(parent.isShare());
            folder.setInherit(true);
        }
        return save(folder);
    }

    private boolean save(Folder folder) {
        try {
            folder = folderMapper.save(folder);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void changeFolderUsage(Folder folder, User user, Files files) {

    }

    @Override

    public List<FilesDTO> AllFolders(User user, String parentName,boolean visible) {
        List<Folder> list=null;

        if (visible) {
            list = folderMapper.findAllByOriginUserAndFolder_ParentNameAndFolder_Visible(user, parentName, visible);
        }else {
            list = folderMapper.findAllByOriginUserAndFolder_ParentName(user,parentName);
        }
        List<FilesDTO> res = new ArrayList<>();
        for (Folder folder : list) {
            res.add(new FolderDTO(folder));
        }
        return res;
    }

    @Override
    public List<FilesDTO> MyShareFolders(User user, String parentName, boolean visible) {
        List<Folder> list=null;
        if (visible) {
            list = folderMapper.findAllByShareUserAndFolder_ParentNameAndFolder_Visible(user, parentName, visible);
        }else {
            list = folderMapper.findAllByShareUserAndFolder_ParentName(user,parentName);
        }
        List<FilesDTO> res = new ArrayList<>();
        for (Folder folder : list) {
            res.add(new FolderDTO(folder));
        }
        return res;
    }
}
