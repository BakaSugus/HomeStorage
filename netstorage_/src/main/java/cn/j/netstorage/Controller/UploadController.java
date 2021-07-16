package cn.j.netstorage.Controller;

import cn.j.netstorage.Entity.Config;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.OssService;
import cn.j.netstorage.Service.UploadService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
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
    @Autowired
    private Config config;

    @PostMapping("/uploadSlice")
    @RequiresPermissions("上传")
    public ResultBuilder<Boolean> uploadSliceFiles(@RequestParam("file") MultipartFile multipartFile,
                                                   @RequestParam("filename") String fileName,
                                                   @RequestParam("chunkSize") int chunkSize,
                                                   @RequestParam("totalChunks") int totalChunks,
                                                   @RequestParam("identifier") String identifier,
                                                   @RequestParam("chunkNumber") int chunkNumber,
                                                   @RequestParam("path") String path) {
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        Boolean result = uploadService.slice_upload(multipartFile, totalChunks, fileName, "", path, chunkNumber, user);
        return new ResultBuilder<>(result, StatusCode.SUCCESS);
    }

    @PostMapping("/checkMd5")
    public ResultBuilder<Boolean> checkMd5(@RequestParam("md5") String md5,
                                           @RequestParam("name") String fileName,
                                           @RequestParam("path") String parentName) {
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        Boolean result = uploadService.checkMd5AndTransfer(md5, parentName, fileName, user);
        return new ResultBuilder<>(result, result ? StatusCode.SUCCESS : StatusCode.FALL);
    }

    @PostMapping("common_upload")
    public ResultBuilder upload(@RequestParam("upload") MultipartFile multipartFile, @RequestParam("parentName") String parentName) {
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        uploadService.common_upload(multipartFile, parentName, user);
        return null;
    }

    @PostMapping("getMapperPath")
    public ResultBuilder getMapperPath(String token, String projectName) {
        String filePath = uploadService.getAutoUploadPath(token, projectName, true);
        if (StringUtils.isEmpty(filePath)) return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder<>(filePath, StatusCode.SUCCESS);
    }


    @PostMapping("/uploadMerge")
    public ResultBuilder Merge(String driver, int size, String fileName, String parentName) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        User user = userService.getUser(object.toString());

        System.out.println((size) + "\t" + fileName + "\t" + parentName);
        Thread runnable =new Thread(new Runnable() {
            @Override
            public void run() {
                boolean res = uploadService.merge_upload(driver,fileName, null, parentName, 1, size, user);
            }
        });
        runnable.start();
        return new ResultBuilder(StatusCode.SUCCESS);
    }

    @Autowired
    private OssService ossService;

    @PostMapping("/Oss/Upload")
    public ResultBuilder OssUpload(String name, Long id) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder<>(StatusCode.FALL);
        return new ResultBuilder<>(ossService.upload(userService.getUser(object.toString()), name, id), StatusCode.SUCCESS);
    }
}
