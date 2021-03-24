import cn.j.netstorage.Entity.Folder.FolderPermission;
import cn.j.netstorage.Mapper.FolderPermissionMapper;
import cn.j.netstorage.NetstorageApplication;
import cn.j.netstorage.Service.FolderService;
import cn.j.netstorage.tool.FilesUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NetstorageApplication.class)
public class FolderServiceTest {

    @Autowired
    private FolderService folderService;

    @Autowired
    private FolderPermissionMapper mapper;
    @Test
    public void add(){
        String [] permissions={"查看","删除","预览","移动","重命名","分享"};
        for (String permission : permissions) {
            FolderPermission p=new FolderPermission();
            p.setPermissionName(permission);
            mapper.save(p);
        }
    }

    @Test
    public void check(){

    }

    @Test
    public void checkPermissions(){
        String [] permissions={"查看","删除","预览","移动","重命名","分享"};
        folderService.permissions(permissions).forEach(System.out::println);
    }

    @Test
    public void change(){

    }

    @Test
    public void share(){
        String [] permissions={"查看","删除","预览","移动","重命名","分享"};
        Set<FolderPermission> folderPermissions=folderService.permissions(permissions);
        Long [] id=new Long[folderPermissions.size()];
        int i=0;
        for (FolderPermission folderPermission : folderPermissions) {
            id[i]=folderPermission.getId();
            i++;
        }
        folderService.shareFolder(56L,id, FilesUtil.setUser(111L));
    }
}
