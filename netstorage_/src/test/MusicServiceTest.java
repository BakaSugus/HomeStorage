import cn.j.netstorage.Entity.plugin.MusicCollection;
import cn.j.netstorage.NetstorageApplication;
import cn.j.netstorage.Service.MusicCollectionService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.FilesUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NetstorageApplication.class)
public class MusicServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private MusicCollectionService musicCollectionService;


    @Test
    public void test(){

        MusicCollection musicCollection=new MusicCollection();
        musicCollection.setUser(FilesUtil.setUser(6L));
        musicCollection.setCollectionName("测试歌单");
        musicCollection.setDate(new Date());
        System.out.println(musicCollectionService.add(musicCollection));
    }

    @Test
    public void addMusic(){
        System.out.println(musicCollectionService.addMusic(51L, 48L, userService.getUser(6L)));

        System.out.println(musicCollectionService.addMusic(51L, 50L, userService.getUser(6L)));

        System.out.println(musicCollectionService.Music(51L));
    }

    @Test
    public void addDevice(){
        System.out.println(musicCollectionService.Musics(FilesUtil.setUser(6L)));

        System.out.println(musicCollectionService.Music(44L));
    }
}
