package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.DTO.FilesVersionDTO;
import cn.j.netstorage.Entity.File.FilesVersion;
import cn.j.netstorage.Entity.User.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FilesVersionService {
    Boolean createFileVersionControl(FilesVersion filesVersion);

    Boolean delete(FilesVersion filesVersion);

    List<FilesVersionDTO> filesVersionList(Long uid);

    Boolean add(FilesVersion filesVersion, MultipartFile multipartFile);

    FilesVersion GetFileVersionByGroupId(Long GroupId);

    List<FilesVersionDTO> GetFileVersionByGroupName(User user, String GroupName);


}
