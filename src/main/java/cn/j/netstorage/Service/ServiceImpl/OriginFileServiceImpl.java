package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Mapper.OriginFileMapper;
import cn.j.netstorage.Service.OriginFileService;
import cn.j.netstorage.tool.HashCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class OriginFileServiceImpl implements OriginFileService{

    @Autowired
    OriginFileMapper originFileMapper;
    @Override
    public OriginFile saveOriginFile(OriginFile originFile) {
        return originFileMapper.save(originFile);
    }

    @Override
    public OriginFile originFile() {
        return null;
    }

    @Override
    public List<OriginFile> originFiles(Long... id) {
        return originFileMapper.findAllById(Arrays.asList(id));
    }

    @Override
    public OriginFile originFile(MultipartFile file, HardDiskDevice hardDiskDevice) throws IOException {
        return originFile(file.getResource().getFile(),hardDiskDevice);
    }

    @Override
    public OriginFile originFile(File file, HardDiskDevice hardDiskDevice) throws IOException {
        String fileName=file.getName();
        OriginFile originFile=new OriginFile();
        originFile.setHardDiskDevice(Collections.singleton(hardDiskDevice));
        originFile.setMd5(HashCodeUtil.getHashCode(file));
        originFile.setFileName(fileName);
        originFile.setSize(file.length());
        return originFile;
    }

    @Override
    public OriginFile originFile(String md5) {
        return originFileMapper.findByMd5(md5);
    }
}
