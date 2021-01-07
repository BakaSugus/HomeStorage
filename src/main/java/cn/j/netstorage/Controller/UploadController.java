package cn.j.netstorage.Controller;

import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.UploadService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeMap;

@RestController
@RequestMapping("/upload/")
public class UploadController {

    @Autowired
    UploadService uploadService;

    @Autowired
    UserService userService;

    @Value("${tempDir}")
    private String tmp;

    @Value("${server.tomcat.basedir}")
    private String baseDir;

    @PostMapping("/uploadSlice")
    public ResultBuilder<Boolean> uploadSliceFiles(@RequestParam("file") MultipartFile multipartFile,
                                                   @RequestParam("filename") String fileName,
                                                   @RequestParam("chunkSize") int chunkSize,
                                                   @RequestParam("totalChunks") int totalChunks,
                                                   @RequestParam("identifier") String identifier,
                                                   @RequestParam("chunkNumber") int chunkNumber,
                                                   @RequestParam("path") String path) {
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        Boolean result = uploadService.slice_upload(multipartFile, totalChunks,fileName, tmp,path, chunkNumber, user);
        return new ResultBuilder<>(result, StatusCode.SUCCESS);
    }

    @PostMapping("/checkMd5")
    public ResultBuilder<Boolean> checkMd5(@RequestParam("md5") String md5,
                                           @RequestParam("name") String fileName,
                                           @RequestParam("path") String parentName) {
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        Boolean result = uploadService.checkMd5AndTransfer(md5, parentName, fileName, user);
        return new ResultBuilder(result, result ? StatusCode.SUCCESS : StatusCode.FALL);
    }

//    @PostMapping("/merge")
//    public ResultBuilder merge(@RequestParam("path") String filePath, @RequestParam("name") String fileName, @RequestParam("size") int size) {
//        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
//        Boolean result = uploadService.merge_upload(fileName, tmp, filePath, 0, size, user);
//        return new ResultBuilder(result, result ? StatusCode.SUCCESS : StatusCode.FALL);
//    }

    @PostMapping("common_upload")
    public ResultBuilder upload(@RequestParam("upload") MultipartFile multipartFile, @RequestParam("parentName") String parentName) {
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        uploadService.common_upload(multipartFile, parentName, user);
        return null;
    }

    @PostMapping("/transferDiskFiles")
    public ResultBuilder transferExistFiles(@RequestBody TreeMap<String, Object> treeMap) {
//        首先转移文件 然后添加原始文件 然后插入文件数据库
        if (treeMap.get("filePath") != null) {

        }
        return new ResultBuilder(StatusCode.SUCCESS);
    }
}
