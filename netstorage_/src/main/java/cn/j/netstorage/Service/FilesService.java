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


    List<FilesDTO> UserFile(String path, User user, boolean visible);

    FilesDTO getFilesById(long fid);

    OriginFile findByParentNameAndAndUserAndAndSelfName(String parentName, User user, String selfName);

    Files findByFid(Long fid);

    Boolean saveOriginFiles(OriginFile originFile);

    OriginFile insertFolder();

    List<Files> getByType(User user, Type type);

    List<Files> searchFiles(FilesDTO filesDTO, User user);

    List<FilesDTO> filesToDTO(List<Files> files, List<FilesDTO> target);

}
