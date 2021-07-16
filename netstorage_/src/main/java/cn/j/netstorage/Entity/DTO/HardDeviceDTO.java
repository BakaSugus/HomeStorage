package cn.j.netstorage.Entity.DTO;

import cn.j.netstorage.Entity.Driver.Driver;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;

@Setter
@Getter
@ToString
public class HardDeviceDTO {
    public String name;
    public String folderPath;
    public String mapperPath;
    public String type;
    public long use;
    public long free;
    public long total;
    public long typeUse;

    public HardDeviceDTO(HardDiskDevice diskDevice) {
        this.name = diskDevice.getDeviceName();
        this.folderPath = diskDevice.getFolderName();
        this.mapperPath = diskDevice.getCustomName();
        this.type = diskDevice.getType();
        File file = diskDevice.get();
        this.use = file.getUsableSpace();
        this.total = file.getTotalSpace();
        this.free = file.getFreeSpace();
        this.typeUse = getTotalSizeOfFilesInDir(file);
    }

    public HardDeviceDTO(Driver driver) {
        this.folderPath = String.format("Remote Driver %s", driver.getType());
        this.mapperPath = driver.getMapper();
        this.type = driver.getType();
        this.name = driver.getBucketName();
    }

    private long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }
}
