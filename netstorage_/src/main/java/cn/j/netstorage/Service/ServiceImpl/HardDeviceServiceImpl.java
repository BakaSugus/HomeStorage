package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.Config;
import cn.j.netstorage.Entity.DTO.HardDeviceDTO;
import cn.j.netstorage.Entity.Driver.Driver;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Mapper.HardDeviceMapper;
//import cn.j.netstorage.Mapper.StorageUsageMapper;
import cn.j.netstorage.Service.DriverService;
import cn.j.netstorage.Service.HardDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HardDeviceServiceImpl implements HardDeviceService {

    @Autowired
    HardDeviceMapper hardDeviceMapper;

    @Autowired
    private DriverService driverService;
    @Autowired
    private Config config;

    @Override
    public Boolean update(HardDiskDevice hardDiskDevice) {
        return null;
    }

    @Override
    public Boolean add(HardDiskDevice hardDiskDevice) {
        return hardDeviceMapper.save(hardDiskDevice).getId() != 0;
    }

    @Override
    public Boolean del(HardDiskDevice hardDiskDevice) {
        return null;
    }

    @Override
    public Boolean move(HardDiskDevice hardDiskDevice1, HardDiskDevice hardDiskDevice2) {
        return null;
    }

    @Override
    public HardDiskDevice get(Long id) {
        return hardDeviceMapper.findById(id).get();
    }

    @Override
    public HardDiskDevice get(String type) {
        return hardDeviceMapper.getHardDiskDeviceByRules(type);
    }

    @Override
    public HardDiskDevice get(Type type) {
        return hardDeviceMapper.getHardDiskDeviceByRules(type.getType());
    }

    @Override
    public HardDiskDevice getByFolderName(String folderName) {
        return hardDeviceMapper.findByFolderName(folderName);
    }

    @Override
    public HardDiskDevice getByMapper(String mapper) {
        return null;
    }

    @Override
    public List<HardDeviceDTO> getHardDevices() {
        List<HardDiskDevice> list = hardDeviceMapper.findAll();
        List<HardDeviceDTO> res = new ArrayList<>();
        for (HardDiskDevice device : list) {
            res.add(new HardDeviceDTO(device));
        }

        List<Driver> drivers = driverService.getAllDriver();
        for (Driver driver : drivers) {
            res.add(new HardDeviceDTO(driver));
        }
        return res;
    }

    @Override
    public List<HashMap<String, String>> getSpace() {
        List<HardDiskDevice> list = this.hardDeviceMapper.findAll();
        List<HashMap<String, String>> mapList = new ArrayList<>();
        for (HardDiskDevice hardDiskDevice : list) {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            File file = hardDiskDevice.get();
            long free = file.getFreeSpace();
            long total = file.getTotalSpace();
            long use = total - free;
            hashMap.put("PatternPath", file.getPath());
            hashMap.put("free", change(free) + "G");
            hashMap.put("used", change(use) + "G");
            hashMap.put("total", change(total) + "G");
            hashMap.put("bfb", bfb(use, total));
            mapList.add(hashMap);
        }
        return mapList;
    }

    @Override
    public Boolean createDevice(String path) {
        List<HardDiskDevice> hardDiskDevices = this.hardDeviceMapper.findAll();
        File file = null;

        if (StringUtils.hasText(path))
            file = new File(path);
        else
            file = new File(new File("").getAbsolutePath() + "//WorkSpace");
        if (!file.exists()) file.mkdirs();

        if (hardDiskDevices != null && hardDiskDevices.size() == Type.values().length) return true;

        for (Type type : Type.values()) {
            String son_path = new File(file.getAbsolutePath() + "/" + type.getType()).getAbsolutePath();
            File folder = new File(son_path);
            if (!folder.exists()) folder.mkdirs();
            HardDiskDevice device = get(type);
            if (device == null || device.getId() == 0) {
                device = new HardDiskDevice();
                device.setFolderName(son_path);
                device.setCustomName(type.getType());
                device.setDeviceName(type.getType());
                device.setRules(type.getType());
                hardDeviceMapper.save(device);
            }
        }
        return true;
    }

    @Override
    public boolean initDevice(HashMap<String, String> map) {
        boolean res = false;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            File file = new File(value);
            if (!file.isDirectory())
                file = file.getParentFile();
            if (!file.exists())
                file.mkdirs();
            HardDiskDevice device = getByFolderName(file.getAbsolutePath());
            if (device == null || device.getId() == 0) {
                device = new HardDiskDevice();
                device.setFolderName(file.getAbsolutePath());
                device.setCustomName(key);
                device.setDeviceName(key);
                device.setRules(key);
                hardDeviceMapper.save(device);
                res = device.getId() != 0;
            }
        }
        return res;
    }

    @Override
    public Boolean migrate(Type type, String path) {

        if (StringUtils.isEmpty(path)) return false;

        File target = new File(path);

        if (target.exists() | target.mkdirs()) {
            long free = target.getUsableSpace();

            HardDiskDevice hardDiskDevice = hardDeviceMapper.getHardDiskDeviceByRules(type.getType());

            String originFolder = hardDiskDevice.getFolderName();
            File file = new File(originFolder);

            if (!file.exists())
                return false;

            long use = file.getTotalSpace() - file.getUsableSpace();
            if (free < use) return false;

            try {
                org.apache.commons.io.FileUtils.copyDirectory(file, target);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    public static long change(long num) {
        return num / 1024 / 1024 / 1024;
    }

    private static String bfb(Object num1, Object num2) {
        double val1 = Double.valueOf(num1.toString());
        double val2 = Double.valueOf(num2.toString());
        if (val2 == 0) {
            return "0.0%";
        } else {
            DecimalFormat df = new DecimalFormat("#0.00");
            return df.format(val1 / val2 * 100) + "%";
        }
    }

}
