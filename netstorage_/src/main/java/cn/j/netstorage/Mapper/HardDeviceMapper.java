package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.File.HardDiskDevice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HardDeviceMapper extends JpaRepository<HardDiskDevice,Long>{
    HardDiskDevice getHardDiskDeviceByRules(String rule);


}
