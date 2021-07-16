package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.DTO.HardDeviceDTO;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.Type;

import java.util.HashMap;
import java.util.List;

public interface HardDeviceService {

    Boolean update(HardDiskDevice hardDiskDevice);

    Boolean add(HardDiskDevice hardDiskDevice);

    Boolean del(HardDiskDevice hardDiskDevice);

    Boolean move(HardDiskDevice hardDiskDevice1, HardDiskDevice hardDiskDevice2);

    HardDiskDevice get(Long id);

    HardDiskDevice get(String type);

    HardDiskDevice get(Type type);


    HardDiskDevice getByFolderName(String folderName);

    HardDiskDevice getByMapper(String mapper);

    List<HardDeviceDTO> getHardDevices();

    List<HashMap<String,String>> getSpace();

    Boolean createDevice(String path);

    boolean initDevice(HashMap<String,String> map);

    Boolean migrate(Type type,String path);

//    void changeStorageTypeUsage(Files files, User user);
}
