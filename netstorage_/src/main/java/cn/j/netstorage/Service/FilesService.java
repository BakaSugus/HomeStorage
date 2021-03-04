package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.DTO.OriginFileDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public interface FilesService {


    Boolean insertFile();

    List<FilesDTO> UserFile(String path, long uid);

    List<FilesDTO> UserFiles(String path, long uid, Boolean isDir);

    List<FilesDTO> UserFile(Long fid, long uid);

    Boolean deleteUserFiles(long uid, long fid);

    FilesDTO getFilesById(long fid);

    OriginFile findByParentNameAndAndUserAndAndSelfName(String parentName, User user, String selfName);

    List<OriginFile> checkUpload(String md5);

//    Boolean uploadFile(Files file, String preSuffix, MultipartFile tempFile);

    Boolean RenameFile(Files files);

    OriginFileDTO getFileByFileName(String FileName);

    Boolean deleteFolders(String parentName, String selfName, Long fid, Long uid);

    Files findByFid(Long fid);

    Boolean saveOriginFiles(OriginFile originFile);

    OriginFile insertFolder();

    OriginFile insertFiles(List<HardDiskDevice> hardDiskDevices, Files file, MultipartFile tempFile) throws IOException;

    List<Files> getByType(User user, Type type);

    List<Files> searchFiles(FilesDTO filesDTO, User user);

    List<FilesDTO> filesToDTO(List<Files> files,List<FilesDTO> target);

    FilesDTO filesToDTO(Files files);
}
