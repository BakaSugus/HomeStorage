package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.DTO.OriginFileDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Mapper.*;
import cn.j.netstorage.Mapper.FileMapper;
import cn.j.netstorage.Service.FileService2;
import cn.j.netstorage.Service.FilesService;
import cn.j.netstorage.Service.FolderService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.FilesUtil;
import cn.j.netstorage.tool.HashCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class FileServiceImpl implements FilesService {
    @Autowired
    private HardDeviceMapper hardDeviceMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private OriginFileMapper originFileMapper;

    @Autowired
    private FileService2 fileService2;

    @Autowired
    private FolderService folderService;

    @Override
    public List<FilesDTO> UserFile(String path, User user, boolean visible) {
        List<FilesDTO> files1 = new ArrayList<>();
        List<Files> folders = null;
        List<Files> files = null;

        Folder self = folderService.folders(user, path);
        if (self != null) {
            user = self.getOriginUser();
        }

        if (visible) {
            folders = fileMapper.findAllByParentNameAndUserAndTypeAndVisible(path, user, Type.Folder.getType(), true);
            files = fileMapper.findAllByParentNameAndUserAndTypeNotAndVisible(path, user, Type.Folder.getType(), true);
        } else {
            System.out.println("隐藏文件");
            folders = fileMapper.findAllByParentNameAndUserAndType(path, user, Type.Folder.getType());
            files = fileMapper.findAllByParentNameAndUserAndTypeNot(path, user, Type.Folder.getType());
            System.out.println(path+"\t"+files);
        }

        for (Files folder : folders) {
            FilesDTO filesDTO = new FilesDTO(folder);
            files1.add(filesDTO);
        }

        for (Files file : files) {
            FilesDTO filesDTO = new FilesDTO(file);
            files1.add(filesDTO);
        }

        return files1;
    }

    @Override
    public FilesDTO getFilesById(long fid) {
        Optional<Files> files = fileMapper.findById(fid);
        return new FilesDTO(files.get());
    }


    @Override
    public OriginFile findByParentNameAndAndUserAndAndSelfName(String parentName, User user, String selfName) {
        Folder folder = folderService.folders(user, parentName);
        Files files = null;

        if (folder == null)
            files = fileMapper.findByParentNameAndUserAndSelfName(parentName, user, selfName);
        else {
            Set<User> userSet = folder.getShareUser();
            for (User u : userSet) {
                if (u.getEmailAccount().equals(user.getEmailAccount())) {
                    files = fileMapper.findByParentNameAndUserAndSelfName(parentName, folder.getOriginUser(), selfName);
                }
            }
        }

        if (files == null)
            return null;

        return files.getOriginFile() != null ? files.getOriginFile() : new OriginFile();
    }

    @Override
    public OriginFile insertFolder() {
        OriginFile originFile = originFileMapper.getOriginFileByFileName("FileType");
        if (originFile == null || originFile.getOid() == 0) {
            originFile = new OriginFile();
            originFile.setFileName("FileType");
            originFile.setMd5(System.currentTimeMillis() + "");
            originFileMapper.save(originFile);
        }
        return originFile;
    }


    private OriginFile getOriginFile(HardDiskDevice hardDiskDevice, Files files, File file) {
        String originName = hardDiskDevice.getFolderName()
                + System.currentTimeMillis()
                + FilesUtil.getExt(file.getAbsolutePath());
        boolean result = file.renameTo(new File(originName));
        OriginFile originFile = null;
        if (result) {
            originFile = new OriginFile();
            originFile.setHardDiskDevice(Collections.singleton(hardDiskDevice));
            originFile.setSize(file.length());
            originFile.setFileName(originName);
            try {
                originFile.setMd5(HashCodeUtil.getHashCode(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            originFile = originFileMapper.save(originFile);
        }
        return originFile.getOid() == 0L ? null : originFile;
    }

    public Boolean uploadFile(Files data, File file) {
        Type type = Type.getInstance(file.getName());
        HardDiskDevice dev = hardDeviceMapper.getHardDiskDeviceByRules(type.getType());
        OriginFile originFile = getOriginFile(dev, data, file);
        if (originFile != null) {
            data.setOriginFile(Collections.singleton(originFile));
            data.setCreateDate(new Date());
            data = fileMapper.save(data);
        }
        return data.getFid() != 0;
    }


    @Override
    public Files findByFid(Long fid) {
        return fileMapper.findById(fid).orElse(null);
    }

    @Override
    public Boolean saveOriginFiles(OriginFile originFile) {
        OriginFile originFile1 = originFileMapper.save(originFile);
        return originFile1.getOid() != 0;
    }


    @Override
    public List<Files> getByType(User user, Type type) {
        List<Files> files = fileMapper.findByUserAndType(user, type.getType());
        return files;
    }

    @Override
    public List<Files> searchFiles(FilesDTO filesDTO, User user) {
        List<FilesDTO> filesDTOS = new ArrayList<>();
        return fileMapper.findAllBySelfNameContainingAndUserIs(filesDTO.getSelfName(), user);
    }

    @Override
    public List<FilesDTO> filesToDTO(List<Files> files, List<FilesDTO> target) {
        if (files != null && files.size() > 1) {
            files.forEach(value -> {
                target.add(new FilesDTO(value));
            });
        }
        return target;
    }

}
