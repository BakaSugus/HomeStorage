package cn.j.netstorage.Controller;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.DTO.OriginFileDTO;
import cn.j.netstorage.Entity.DTO.UserDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.oss.Bucket;
import cn.j.netstorage.Service.*;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.zip.ZipOutputStream;

@RestController
@RequiresUser
@RequestMapping("/disk/")
public class FileController {

    @Autowired
    private FilesService filesService;

    @Autowired
    private FileService2 fileService2;

    @Autowired
    private UserService userService;

    @Autowired
    private Aria2Service aria2Service;

    @Autowired
    private DeleteService deleteService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    FolderService folderService;

    @PostMapping("/uploadFolder")
    @RequiresUser
    @RequiresPermissions("上传")
    public ResultBuilder<Boolean> createFolder(@RequestBody FilesDTO filesDTO) {
        UserDTO userDTO = new UserDTO(userService.getUser(SecurityUtils.getSubject().getPrincipal().toString()));
        Files files = filesDTO.ConvertFiles();
        files.setUser(userDTO.convertUsers());
        Boolean result = uploadService.common_upload_Folder(files);
        return new ResultBuilder<>(result, result ? StatusCode.SUCCESS : StatusCode.FALL);
    }


    @GetMapping("/fileList")
    @RequiresUser
    public ResultBuilder<List<FilesDTO>> getFileList(String parentName, String path) {
        UserDTO userDTO = null;
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder<>(StatusCode.FALL);
        userDTO = new UserDTO(userService.getUser(object.toString()));
        List<FilesDTO> fileList = filesService.UserFile(parentName, userDTO.getUid());
        return new ResultBuilder<>(fileList, StatusCode.SUCCESS);
    }

    @GetMapping("/type")
    @RequiresUser
    public ResultBuilder<List<FilesDTO>> getFileListByType(String type) {
        UserDTO userDTO = new UserDTO(userService.getUser(SecurityUtils.getSubject().getPrincipal().toString()));
        List<FilesDTO> fileList = filesService.UserFile(type, userDTO.getUid());
        return new ResultBuilder<>(fileList, StatusCode.SUCCESS);
    }

    @DeleteMapping("/delete")
    @RequiresUser
    public ResultBuilder deleteFileById(Long... id) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        Boolean res = deleteService.DeleteFiles(userService.getUser(object.toString()), id);
        return new ResultBuilder<>(res, StatusCode.FALL);
    }

    @PutMapping("/Rename/{id}")
    @RequiresUser
    public ResultBuilder<Boolean> Rename(@PathVariable("id") Long fid, String targetName) {
        if (SecurityUtils.getSubject().getPrincipal() != null) {
            User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
            fileService2.RenameFile(user, fid, targetName);
            return new ResultBuilder<>(StatusCode.SUCCESS);
        }
        return new ResultBuilder<>(StatusCode.REQUEST_PARAM_ERROR);
    }

    @GetMapping("/getFile")
    @RequiresUser
    public ResultBuilder<OriginFileDTO> Files(@RequestParam String pathName, @RequestParam String fileName) {
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        try {
            pathName = URLDecoder.decode(pathName, "UTF-8");
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResultBuilder<>(StatusCode.REQUEST_PARAM_ERROR);
        }

        OriginFile originFile = (filesService.findByParentNameAndAndUserAndAndSelfName(pathName, user, fileName));
        if (originFile == null)
            return new ResultBuilder<>(StatusCode.FALL);
        return new ResultBuilder<>(new OriginFileDTO(originFile), StatusCode.SUCCESS);
    }

    @GetMapping("/getFileById")
    @RequiresUser
    public ResultBuilder<OriginFileDTO> getFiles(String fid) {
        FilesDTO filesDTO = filesService.getFilesById(Long.valueOf(fid));
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        return new ResultBuilder<OriginFileDTO>(new OriginFileDTO(filesService.findByParentNameAndAndUserAndAndSelfName(filesDTO.getParentName(), user, filesDTO.getSelfName())), StatusCode.SUCCESS);
    }

    @GetMapping("/searchFiles")
    @RequiresUser
    public ResultBuilder<List<FilesDTO>> searchFiles(String keyword) {
        FilesDTO filesDTO = new FilesDTO();
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        filesDTO.setSelfName(keyword);

        List<FilesDTO> dtos = filesService.filesToDTO(filesService.searchFiles(filesDTO, user), new ArrayList<>());
        return new ResultBuilder<>(dtos, StatusCode.SUCCESS);
    }

    @PutMapping("/folder")
    @RequiresUser
    @RequiresPermissions(value = "共享")
    public ResultBuilder<Boolean> shareFolder(Long fid,Long [] folderPermission, String email) {
        User user = userService.getUser(email);
        Boolean result = folderService.shareFolder(fid, folderPermission,user);
        System.out.println(result);
        return new ResultBuilder<>(result, result ? StatusCode.SUCCESS : StatusCode.FALL);
    }

    @GetMapping("/folder")
    @RequiresUser
    public ResultBuilder<List<FilesDTO>> getShareFolder() {
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        //查出所有该用户拥有的共享文件夹
        return new ResultBuilder<>(folderService.folders(user), StatusCode.SUCCESS);
    }

    @GetMapping("/folder/{id}")
    public ResultBuilder getShareFolder(@PathVariable("id") Long id) {
        return new ResultBuilder<>(folderService.folders(id), StatusCode.SUCCESS);
    }

    @DeleteMapping("/folder/{id}")
    public ResultBuilder deleteShareFolder(@PathVariable("id") Long id) {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj == null)
            return new ResultBuilder(StatusCode.FALL);

        boolean result = folderService.delete(id, userService.getUser(obj.toString()));
        return new ResultBuilder(result ? StatusCode.SUCCESS : StatusCode.FALL);
    }

    @PostMapping("/folder/changePermission")
    public ResultBuilder changePermission(Long id,Long [] pid){
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj == null)
            return new ResultBuilder(StatusCode.FALL);
        Boolean res=folderService.changePermission(id,pid,null,userService.getUser(obj.toString()));
        return new ResultBuilder<>(res,StatusCode.SUCCESS);
    }

    @GetMapping("/folderPermissions")
    public ResultBuilder Permissions(){
        return new ResultBuilder<>(folderService.permissions(),StatusCode.SUCCESS);
    }

    @GetMapping("/MyShareFolder")
    public ResultBuilder Shares(){
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj == null)
            return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder<>(folderService.MyFolders(userService.getUser(obj.toString())),StatusCode.SUCCESS);
    }

    @GetMapping("/ShareToMe")
    public ResultBuilder ShareToMe(){
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj == null)
            return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder<>(folderService.ShareToMe(userService.getUser(obj.toString())),StatusCode.SUCCESS);
    }

    @PostMapping("/download")
    @ResponseBody
    public void download(HttpServletResponse response, Long... fid) {
        try {
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + new String("压缩包名称.zip".getBytes("GB2312"), "ISO-8859-1"));  // 需要编码否则中文乱码
            response.setContentType("application/zip;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(response.getOutputStream());
            fileService2.zip(zipOutputStream, user, fid);
            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
