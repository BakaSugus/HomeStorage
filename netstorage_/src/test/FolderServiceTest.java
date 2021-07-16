import cn.j.netstorage.NetstorageApplication;
import cn.j.netstorage.Service.FolderService;
import cn.j.netstorage.Service.UploadService;
import cn.j.netstorage.tool.FilesUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NetstorageApplication.class)
public class FolderServiceTest {

    @Autowired
    private FolderService folderService;

    @Autowired
    private UploadService uploadService;

    @Test
    public void check(){
        folderService.ShareToMe(FilesUtil.setUser(88L)).forEach(value->{
            System.out.println(value.getName());
        });
    }

    @Test
    public void shareToMe(){
        System.out.println(folderService.ShareToMe(FilesUtil.setUser(88L)));
    }

    @Test
    public void change(){
        System.out.println(uploadService.getAutoUploadPath("testkey", "测试",true));
    }

    @Test
    public void upload(){
        System.out.println(uploadService.AutoUploadComplete("testkey", "测试"));
    }

//    @Test
//    public void share(){
//
//        StringBuilder sb=new StringBuilder();
//        sb.append(FFMPEG.SplitMusic("C:\\Users\\Shinelon\\Downloads\\mda-kbtjuzsi02cuth4x.mp4","C:\\Users\\Shinelon\\Downloads\\test.mp3"));
//
//        cmdExecut(sb.toString());
//    }
//
//    public static Integer cmdExecut(String cmdStr) {
//        //code=0表示正常
//        Integer code  = null;
//        Ffmpeg ffmpegCmd = new Ffmpeg();
//        /**
//         * 错误流
//         */
//        InputStream errorStream = null;
//        try {
//            //destroyOnRuntimeShutdown表示是否立即关闭Runtime
//            //如果ffmpeg命令需要长时间执行，destroyOnRuntimeShutdown = false
//
//            //openIOStreams表示是不是需要打开输入输出流:
//            //	       inputStream = processWrapper.getInputStream();
//            //	       outputStream = processWrapper.getOutputStream();
//            //	       errorStream = processWrapper.getErrorStream();
//            ffmpegCmd.execute(false, true, cmdStr);
//            errorStream = ffmpegCmd.getErrorStream();
//
//            //打印过程
//            int len = 0;
//            while ((len=errorStream.read())!=-1){
//                System.out.print((char)len);
//            }
//
//            //code=0表示正常
//            code = ffmpegCmd.getProcessExitCode();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            //关闭资源
//            ffmpegCmd.close();
//        }
//        //返回
//        return code;
//    }
}
