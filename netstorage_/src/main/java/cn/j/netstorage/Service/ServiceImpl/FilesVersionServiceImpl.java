package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.DTO.FilesVersionDTO;
import cn.j.netstorage.Entity.File.FilesVersion;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Mapper.FilesVersionMapper;
import cn.j.netstorage.Mapper.HardDeviceMapper;
import cn.j.netstorage.Mapper.OriginFileMapper;
import cn.j.netstorage.Service.FilesVersionService;
import cn.j.netstorage.tool.FilesUtil;
import cn.j.netstorage.tool.HashCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class FilesVersionServiceImpl implements FilesVersionService {
    @Autowired
    FilesVersionMapper filesVersionMapper;
    @Autowired
    OriginFileMapper originFileMapper;
    @Autowired
    HardDeviceMapper hardDeviceMapper;

    @Override
    public Boolean createFileVersionControl(FilesVersion filesVersion) {
        filesVersion = filesVersionMapper.save(filesVersion);
        System.out.println(filesVersion.getGroupId());
        return filesVersion.getGroupId() != 0;
    }

    @Override
    public Boolean delete(FilesVersion filesVersion) {
        try {
            filesVersionMapper.delete(filesVersion);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<FilesVersionDTO> filesVersionList(Long uid) {
        List<FilesVersion> versionList=filesVersionMapper.findAllByUsers(FilesUtil.setUser(uid));
        HashMap<String,FilesVersionDTO> hashMap=new HashMap<>();
        for (FilesVersion version:versionList) {
            if (hashMap.containsKey(version.getGroupName())){
                FilesVersionDTO filesVersionDTO=new FilesVersionDTO();
                hashMap.get(version.getGroupName()).Add(filesVersionDTO.ConvertFileVersionDTO(version));
            }else{
                FilesVersionDTO filesVersionDTO=new FilesVersionDTO();
                hashMap.put(version.getGroupName(),filesVersionDTO.ConvertFileVersionDTO(version));
            }
        }
        hashMap.values().forEach(FilesVersionDTO::CheckChildren);
        return new ArrayList<FilesVersionDTO>(hashMap.values());
    }

    @Override
    public Boolean add(FilesVersion filesVersion, MultipartFile multipartFile) {
        try {
            String ext = FilesUtil.getExt(filesVersion.getGroupName());
            OriginFile originFile = new OriginFile();
            String originName=(System.currentTimeMillis() + ext);
            originFile.setFileName(originName);
            originFile.setMd5(HashCodeUtil.getHashCode(multipartFile));
            List<HardDiskDevice> hardDiskDevices = hardDeviceMapper.findAll();
            HardDiskDevice hardDiskDevice= hardDiskDevices.get(new Random().nextInt(hardDiskDevices.size()));
            originFile.setHardDiskDevice(Collections.singleton(hardDiskDevice));
            originFile.setSize(multipartFile.getSize());
            originFile=originFileMapper.save(originFile);
            multipartFile.transferTo(new File(hardDiskDevice.getFolderName()+"/"+originName));

            filesVersion.setUpdateDate(new Date());
            filesVersion.setOriginFileSet(Collections.singleton(originFile));

            filesVersion = filesVersionMapper.save(filesVersion);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filesVersion.getGroupId() != 0;
    }

    @Override
    public FilesVersion GetFileVersionByGroupId(Long GroupId) {
        return filesVersionMapper.findById(GroupId).orElse(null);
    }

    @Override
    public List<FilesVersionDTO> GetFileVersionByGroupName(User user, String GroupName) {
        FilesVersion filesVersion=new FilesVersion();
        filesVersion.setGroupName(GroupName);

        Example<FilesVersion> example = Example.of(
                filesVersion,
                ExampleMatcher.matching().withIgnorePaths("group_id", "version", "update_date", "desc_"));
        List<FilesVersion> filesVersions=filesVersionMapper.findAll(example);
        List<FilesVersionDTO> filesVersionDTOS=new ArrayList<>();

        for (FilesVersion version : filesVersions) {
            filesVersionDTOS.add(new FilesVersionDTO().ConvertFileVersionDTO(version));
        }
        return filesVersionDTOS;
    }
}
