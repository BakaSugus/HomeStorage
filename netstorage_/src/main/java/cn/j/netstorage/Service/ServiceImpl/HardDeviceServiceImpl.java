package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Mapper.HardDeviceMapper;
import cn.j.netstorage.Service.HardDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Service
public class HardDeviceServiceImpl implements HardDeviceService {

    @Autowired
    HardDeviceMapper hardDeviceMapper;

    private Type[] typeList = Type.values();

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
    public HardDiskDevice get(Type type) {
        return hardDeviceMapper.getHardDiskDeviceByRules(type.getType());
    }

    @Override
    public List<HardDiskDevice> getHardDevices() {
        return hardDeviceMapper.findAll();
    }

    @Override
    public List<HashMap<String, String>> getSpace() {
        List<HardDiskDevice> list = this.getHardDevices();
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


    @Value("${workSpace}")
    private String workSpace;

    @Override
    public Boolean createDevice() {
        List<HardDiskDevice> hardDiskDevices = getHardDevices();
        System.out.println(hardDiskDevices);
        if (hardDiskDevices == null ||hardDiskDevices.size() == 0) {
            File file = null;

            if (StringUtils.hasText(workSpace))
                file = new File(workSpace);
            else
                file = new File(new File("").getAbsolutePath()+"//WorkSpace");

            if (!file.exists()) file.mkdirs();

            for (Type type : Type.values()) {
                String path = new File(file.getAbsolutePath() + "/" + type.getType()).getAbsolutePath();
                if (new File(path).mkdirs()) {
                    HardDiskDevice hardDiskDevice = new HardDiskDevice();
                    hardDiskDevice.setFolderName(path);
                    hardDiskDevice.setCustomName(type.getType());
                    hardDiskDevice.setDeviceName(type.getType());
                    hardDiskDevice.setRules(type.getType());
                    hardDeviceMapper.save(hardDiskDevice);
                }
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    public Boolean migrate(Type type, String path) {
        File target = new File(path);

        if (!target.exists())
            return false;
        HardDiskDevice hardDiskDevice = hardDeviceMapper.getHardDiskDeviceByRules(type.getType());

        String originFolder = hardDiskDevice.getFolderName();
        File file = new File(originFolder);

        if (!file.exists())
            return false;

        File[] files = file.listFiles();
//        //计算容量是否能够容纳
//        Long res=0L;
//        for (int i = 0; i < files.length; i++) {
//            res+=files[i].length();
//        }
//
//        if (file.getFreeSpace()<res)
//            return false;

        for (File f : files) {
            f.renameTo(new File(file.getAbsolutePath() + "/" + f.getName()));
        }
        return true;
    }

    public static long change(long num) {
        // return num;
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
