import cn.j.netstorage.NetstorageApplication;
import cn.j.netstorage.Service.Aria2Service;
import cn.j.netstorage.tool.FilesUtil;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Test;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NetstorageApplication.class)
public class Aria2DownloadTest {

    @Autowired
    Aria2Service aria2Service;

    @Test
    public void download() {
        aria2Service.download("http://m701.music.126.net/20210303195832/7170bf045ec864d1609355f2a85a2a00/jdymusic/obj/wo3DlMOGwrbDjj7DisKw/4959745806/482a/1a84/ca27/02c0f32c8c1b78a97988cebbee2ede1b.mp3", "/", FilesUtil.setUser(6L));
    }

    @Test
    public void addTorrent() {
        System.out.println(aria2Service.getActive(FilesUtil.setUser(6L)));
        System.out.println(aria2Service.getStopped(FilesUtil.setUser(6L)));
        System.out.println(aria2Service.getWaiting(FilesUtil.setUser(6L)));
    }
}
