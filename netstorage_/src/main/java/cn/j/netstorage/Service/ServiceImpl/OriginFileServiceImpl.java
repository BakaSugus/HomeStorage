package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.MapperConfig;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Mapper.OriginFileMapper;
import cn.j.netstorage.Service.HardDeviceService;
import cn.j.netstorage.Service.OriginFileService;
import cn.j.netstorage.tool.HashCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class OriginFileServiceImpl implements OriginFileService {

    @Autowired
    OriginFileMapper originFileMapper;

    @Autowired
    HardDeviceService hardDeviceService;

    @Override
    public OriginFile saveOriginFile(OriginFile originFile) {
        if (originFile==null)return null;
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
        return originFile(file.getResource().getFile(), hardDiskDevice);
    }

//    public OriginFile originFile(InputStream content, HardDiskDevice hardDiskDevice,String self) throws IOException {
//        OutputStream outputStream=new FileOutputStream(new File(hardDiskDevice.getFolderName(),self));
//        byte[] buffer = new byte[4096];
//        int bytes = content.read(buffer, 0, buffer.length);
//        while (bytes != -1) {
//            .write(buffer, 0, bytes);
//            bytes = bfi.read(buffer, 0, buffer.length);
//        }
//        return originFile(file.getResource().getFile(), hardDiskDevice);
//    }

    @Override
    public OriginFile originFile(File file, HardDiskDevice hardDiskDevice) throws IOException {
        boolean res = file.exists();
        if (!res) {
            res = file.createNewFile();
        }
        if (!res) return null;
        String fileName = file.getName();
        OriginFile originFile = new OriginFile();
        originFile.setHardDiskDevice(Collections.singleton(hardDiskDevice));
        originFile.setMd5(HashCodeUtil.getHashCode(file));
        originFile.setFileName(fileName);
        originFile.setSize(file.length());
        return originFile;
    }

    @Override
    public OriginFile originFile(String originFileName, HardDiskDevice hardDiskDevice) throws IOException {
        return originFileMapper.getOriginFileByFileNameAndHardDiskDevice(originFileName, hardDiskDevice);
    }

    @Override
    public OriginFile originFile(String md5) {
        return originFileMapper.findByMd5(md5);
    }

    @Override
    public boolean deleteOriginFile(List<OriginFile> originFiles) {
        try {
            for (OriginFile originFile : originFiles) {
                new File(originFile.getPath()).delete();
            }
            originFileMapper.deleteAll(originFiles);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public int count(String path) {
        if (StringUtils.isEmpty(path)) return -1;
        return originFileMapper.countAllByFileName(path);
    }

    public HashMap<String, String> getProperties() {
        HardDiskDevice device = hardDeviceService.get(Type.Setting);
        OriginFile originFile = null;
        try {
            originFile = originFile("config.properties", device);
            if (originFile != null) return null;
            Properties properties = new Properties();
            File file = device.get();
            file = new File(file, "config.properties");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            properties.load(bufferedReader);
            String change = properties.getProperty("auto_encoding", "false");
            String size = properties.getProperty("size", "100");
            return MapperConfig.getCustomTemplate(change, size);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();

    }

}
