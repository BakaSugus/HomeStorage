package cn.j.netstorage.Config;

import cn.j.netstorage.Entity.Config;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.UploadService;
import cn.j.netstorage.Service.UserService;
import com.qcloud.cos.transfer.Upload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

public class GenerateBackup {
//    @Value("${spring.datasource.url}")
//    private String url;
//    @Value("${spring.datasource.username}")
//    private String username;
//    @Value("${spring.datasource.password}")
//    private String password;
//    @Value("${spring.datasource.driver}")
//    private String driver;
//    @Value("${spring.datasource.type}")
//    private String type;
//    @Value("${server.port}")
//    private int port;
//
//    @Autowired
//    private Config config;
//
//    @Autowired
//    private UploadService uploadService;
//
//    @Autowired
//    private UserService userService;
//
//    public void generate() {
////        String token=getToken();
////        String path=getPath(token);
////        String cmd="";
////        //RunTime
////        autoUpload(token);
//    }
//
//    public String getToken() {
//        User user = this.config.getAdmin();
//        if (user == null || StringUtils.isEmpty(user.getEmailAccount())) return null;
//        return userService.getUserByRole(userService.role("admin")).getToken();
//    }
//
//    public String getPath(String Token) {
//        //直接通知uploadService
//        return uploadService.getAutoUploadPath(Token, "BK_SQL",true);
//    }
//
//    public boolean autoUpload(String Token) {
//        //直接通知uploadService
//        return uploadService.AutoUploadComplete(Token, "BK_SQL");
//    }
}
