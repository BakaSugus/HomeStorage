package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.DTO.FolderDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.Folder.FolderPermission;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Mapper.FolderMapper;
import cn.j.netstorage.Mapper.FolderPermissionMapper;
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
    private FolderPermissionMapper mapper;
    @Autowired
    private FileService2 fileService2;

    public Folder getFolder(Files files) {
        return folderMapper.findByFolder(files);
    }

    public List<FilesDTO> folders(User user) {
        List<FilesDTO> filesDto = new ArrayList<>();
        List<Folder> list = folderMapper.findByShareUser(user);
        for (Folder f :
                list) {
            filesDto.add(new FilesDTO(f.getFolder()));
        }
        return filesDto;
    }


    @Override
    public Boolean changePermission(Long folderId, Long[] id, User user, User originUser) {
        Set<FolderPermission> permissions = permissions(id);
        Folder folder = folders(folderId).get(0);
        if (folder.getOriginUser() != originUser) return false;
        folder.setPermissions(permissions);
        folderMapper.save(folder);
        return true;
    }

    @Override
    public Boolean delete(Long id, User user) {
        return null;
    }

    @Override
    public Boolean checkPermission(String permission, User user) {
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
    public Folder folders(Files files) {
        if (files==null)return null;
        return folderMapper.findByFolder(files);
    }

    @Override
    public List<FolderDTO> MyFolders(User user) {
        if (user==null)return null;
        List<FolderDTO> list=new ArrayList<>();
        List<Folder> folders=folderMapper.findAllByOriginUser(user);
        folders.forEach(value->list.add(new FolderDTO(value)));
        return list;
    }

    @Override
    public List<FolderDTO> ShareToMe(User user) {
        if (user==null)return null;
        List<FolderDTO> list=new ArrayList<>();
        List<Folder> folders=folderMapper.findByShareUser(user);
        folders.forEach(value->list.add(new FolderDTO(value)));
        return list;
    }

    @Override
    public Set<FolderPermission> permissions() {
        return new HashSet<>(mapper.findAll());
    }

    @Override
    public Set<FolderPermission> permissions(String... name) {
        return mapper.findByPermissionNameIn(name);
    }

    @Override
    public Set<FolderPermission> permissions(Long... id) {
        return new HashSet<>(mapper.findAllById(Arrays.asList(id)));
    }

    @Override
    public boolean shareFolder(Long fid, User user) {
        Set<FolderPermission> permissions = this.permissions("查看", "预览");
        return shareFolder(fid, permissions, user);
    }

    public boolean shareFolder(Long fid, User user,String ... permission) {
        Set<FolderPermission> permissions = this.permissions(permission);
        return shareFolder(fid, permissions, user);
    }

    public boolean shareFolder(Long fid, Set<FolderPermission> permissions, User user) {
        Files files = fileService2.files(fid).get(0);
        if (files == null) {
            return false;
        }
        if (!files.getType().equals(Type.Folder.getType())) {
            return false;
        }

        Folder folder = getFolder(files);

        if (folder == null) {
            folder = new Folder();
        } else {
            return false;
        }

        folder.setFolder(files);
        folder.setFolderName(files.getParentName() + files.getSelfName() + "/");
        folder.setOriginUser(files.getUser().get(0));

        folder.setShareUser(user);
        folder.setPermissions(permissions);
        return folderMapper.save(folder).getId() != 0;
    }

    @Override
    public boolean shareFolder(Long fid, Long[] permissionId, User user) {
        return shareFolder(fid, permissions(permissionId), user);
    }
}
