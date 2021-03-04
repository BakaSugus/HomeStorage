package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.DTO.OriginFileDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Folder;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Mapper.FileMapper;
import cn.j.netstorage.Mapper.HardDeviceMapper;
import cn.j.netstorage.Mapper.OriginFileMapper;
import cn.j.netstorage.Mapper.UserMapper;
import cn.j.netstorage.Service.FileService2;
import cn.j.netstorage.Service.FilesService;
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
import java.text.DecimalFormat;
import java.util.*;

@Service
public class FileServiceImpl implements FilesService {
    @Autowired
    HardDeviceMapper hardDeviceMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    FileMapper fileMapper;

    @Autowired
    OriginFileMapper originFileMapper;

    @Autowired
    private FileService2 fileService2;

    @Autowired
    private UserService userService;


    @Override
    public Boolean insertFile() {
        return null;
    }

    @Override
    public List<FilesDTO> UserFile(String path, long uid) {
        //先查这个文件夹是不是在共享列表里 如果是就将他的parentName,selfName originUser提取出来
        //如果没有直接查

        User user = userService.getUser(uid);
        Folder folder = fileService2.getFolder(user, path);
        if (folder != null) {
            user = folder.getOriginUser().iterator().next();
        } else {
            user = FilesUtil.setUser(uid);
        }
        List<FilesDTO> files1 = new ArrayList<>();
        fileMapper.findAllByParentNameAndUser_uidAndType(path, (user.getUid()), Type.Folder.getType()).forEach((value) -> {
            FilesDTO filesDTO = new FilesDTO(value);
            files1.add(filesDTO);
        });

        fileMapper.findAllByParentNameAndUser_uidAndTypeNot(path, (user.getUid()), Type.Folder.getType()).forEach((value) -> {
            FilesDTO filesDTO = new FilesDTO(value);
            files1.add(filesDTO);
        });
        return files1;
    }

    /**
     * 用户访问页面返回文件夹和文件的列表
     *
     * @param uid 用户id
     * @return 文件夹和文件的混合列表
     */
    @Override
    public List<FilesDTO> UserFiles(String path, long uid, Boolean b) {
        return null;
    }

    @Override
    public List<FilesDTO> UserFile(Long fid, long uid) {
        List<Files> files = fileService2.files(fid);
        if (files == null || files.size() < 1) {
            return null;
        }
        Files file = files.get(0);
        String path = file.getParentName() + file.getSelfName() + "/";
        User user = userService.getUser(uid);
        Folder folder = fileService2.getFolder(file);
        if (folder != null) {
            return UserFile(path, folder.getOriginUser().iterator().next().getUid());
        } else {
            return UserFile(path, uid);
        }
    }


    /**
     * 删除文件的时候删除文件
     *
     * @param uid 用户id
     * @param fid 文件id
     * @return 删除结果
     */

    @Override
    public Boolean deleteUserFiles(long uid, long fid) {
        Files files = new Files();
        User user = new User();
        user.setUid(uid);
        files.setUser(Collections.singletonList(user));
        files.setFid(fid);
        return FilesUtil.delete(fileMapper, files);
    }

    /**
     * 根据id获得文件
     *
     * @param fid 文件id
     * @return 文件
     */

    @Override
    public FilesDTO getFilesById(long fid) {
        Optional<Files> files = fileMapper.findById(fid);
        return new FilesDTO(files.orElse(null));
    }

    @Override
    public OriginFile findByParentNameAndAndUserAndAndSelfName(String parentName, User user, String selfName) {
        Files files = fileMapper.findByParentNameAndAndUserAndAndSelfName(parentName, user, selfName);
        return files.getOriginFile() != null ? new ArrayList<OriginFile>(files.getOriginFile()).get(0) : new OriginFile();
    }

    /**
     * 避免多次上传的md5检测
     *
     * @param md5 文件的md5值
     * @return 是否存在相同md5结果
     */

    @Override
    public List<OriginFile> checkUpload(String md5) {
        OriginFile originFile = new OriginFile();
        originFile.setMd5(md5);
        Example<OriginFile> example = Example.of(originFile,
                ExampleMatcher.matching().withIgnorePaths("oid", "file_name"));
        return originFileMapper.findAll(example);
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

    @Override
    public OriginFile insertFiles(List<HardDiskDevice> hardDiskDevices, Files file, MultipartFile tempFile) throws IOException {
        int num = file.getSelfName().lastIndexOf(".");
        String fileName_ = file.getSelfName().substring(0, num);
        String exg = file.getSelfName().substring(num);
        if (hardDiskDevices.isEmpty()) {
            return null;
        }
        int randomInt = new Random().nextInt(hardDiskDevices.size());
        HardDiskDevice hardDiskDevice = hardDiskDevices.get(randomInt);

        String ext = file.getSelfName().substring(file.getSelfName().lastIndexOf(".") + 1);
        String fileName = FilesUtil.getCurrentNameWithExt("." + ext);

        File dst = new File(hardDiskDevice.getFolderName() + "/" + fileName);
        tempFile.transferTo(dst);

        OriginFile originFile = new OriginFile();
        originFile.setFileName(fileName);
        String md5 = HashCodeUtil.getHashCode(dst);

        List<OriginFile> originFiles = checkUpload(md5);

        if (checkUpload(HashCodeUtil.getHashCode(dst)).size() >= 1) {
            return originFiles.get(0);
        }

        originFile = new OriginFile();
        originFile.setFileName(fileName);
        originFile.setMd5(md5);
        originFile.setSize(tempFile.getSize());
        originFile.setHardDiskDevice(Collections.singleton(hardDiskDevice));
        originFile = originFileMapper.save(originFile);
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
        List<HardDiskDevice> hardDiskDevices = hardDeviceMapper.findAll();
        OriginFile originFile = getOriginFile(hardDiskDevices.get(new Random().nextInt(hardDiskDevices.size())), data, file);
        if (originFile != null) {
            data.setOriginFile(Collections.singleton(originFile));
            data.setCreateDate(new Date());
            data = fileMapper.save(data);
        }
        return data.getFid() != 0;
    }


    @Override
    public Boolean RenameFile(Files files) {
        return null;
    }

    @Override
    public OriginFileDTO getFileByFileName(String FileName) {
        return new OriginFileDTO(originFileMapper.getOriginFileByFileName(FileName));
    }

    @Override
    @Transactional
    public Boolean deleteFolders(String parentName, String selfName, Long fid, Long uid) {
        return fileMapper.deleteAllByFidIsOrParentNameIsLikeAndUser(fid, parentName + selfName + "/%", FilesUtil.setUser(uid)) > 0 && this.deleteUserFiles(uid, fid);
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

    @Override
    public FilesDTO filesToDTO(Files files) {
        if (files == null) return null;
        return new FilesDTO(files);
    }


}
