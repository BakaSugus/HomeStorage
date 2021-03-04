package cn.j.netstorage.Service;

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

    HardDiskDevice get(Type type);

    List<HardDiskDevice> getHardDevices();

    List<HashMap<String,String>> getSpace();

    Boolean createDevice();

    Boolean migrate(Type type,String path);


}
