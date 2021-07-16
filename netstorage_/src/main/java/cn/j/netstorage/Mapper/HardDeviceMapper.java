package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.File.HardDiskDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HardDeviceMapper extends JpaRepository<HardDiskDevice,Long>{
    HardDiskDevice getHardDiskDeviceByRules(String rule);

//    List<HardDiskDevice> getHardDiskDeviceByDeviceName(String DeviceName);

    HardDiskDevice findByFolderName(String folderName);

   // HardDiskDevice findByCustomName(String CustomName);
}
