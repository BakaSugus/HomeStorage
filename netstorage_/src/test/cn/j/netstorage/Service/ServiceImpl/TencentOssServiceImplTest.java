package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.File.OriginFile;

import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.oss.Oss;
import cn.j.netstorage.NetstorageApplication;
import cn.j.netstorage.Service.OriginFileService;
import cn.j.netstorage.Service.OssService;
import cn.j.netstorage.Service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NetstorageApplication.class)
public class TencentOssServiceImplTest {
    @Autowired
    OssService ossService;
    @Autowired
    private UserService service;

    @Test
    public void getBackupBucket() {
    }


//    @Test
//    public void get() {
//    }
//
//    @Test
//    void upload() {
//    }
//
//    @Test
//    void download() {
//    }
//
//    @Test
//    void delete() {
//    }
}