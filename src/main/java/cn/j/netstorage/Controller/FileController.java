package cn.j.netstorage.Controller;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.DTO.OriginFileDTO;
import cn.j.netstorage.Entity.DTO.UserDTO;
import cn.j.netstorage.Entity.File.Aria2File;
import cn.j.netstorage.Entity.File.File;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.Aria2Service;
import cn.j.netstorage.Service.FilesService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.EncrypDes;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.message.AuthException;
import javax.xml.transform.Result;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

@RestController
@RequiresUser
@RequestMapping("/disk/")
public class FileController {

    @Autowired
    private FilesService filesService;

    @Autowired
    private UserService userService;

    @Autowired
    private Aria2Service aria2Service;

    @PostMapping("/uploadFolder")
    @RequiresPermissions("上传")
    public ResultBuilder<Boolean> createFolder(@RequestBody FilesDTO filesDTO) {
        UserDTO userDTO = new UserDTO(userService.getUser(SecurityUtils.getSubject().getPrincipal().toString()));
        filesDTO.setIsDir(Files.is_dir);
        filesDTO.setCreateDate(new Date());
        Files files = filesDTO.ConvertFiles();
        files.setUser(userDTO.convertUsers());
        Boolean result = filesService.uploadFile(files, null,null);
        return new ResultBuilder<>(result, result ? StatusCode.SUCCESS : StatusCode.FALL);
    }

    @PostMapping("/uploadFile")
    @RequiresPermissions("上传")
    public ResultBuilder<Boolean> UploadFile(@RequestParam("upload") MultipartFile multipartFile, @RequestParam("parentName") String parentName) {
        FilesDTO filesDTO = new FilesDTO();
        UserDTO userDTO = new UserDTO(userService.getUser(SecurityUtils.getSubject().getPrincipal().toString()));
        filesDTO.setIsDir(Files.no_dir);
        filesDTO.setCreateDate(new Date());
        filesDTO.setSelfName(multipartFile.getOriginalFilename());
        filesDTO.setParentName(parentName);

        Files files = filesDTO.ConvertFiles();
        files.setUser(userDTO.convertUsers());
        Boolean result = filesService.uploadFile(files, null, multipartFile);
        return new ResultBuilder<>(result, result ? StatusCode.SUCCESS : StatusCode.FALL);
    }

    @PostMapping("/transferExistFiles")
    public ResultBuilder transferExistFiles(@RequestBody TreeMap<String,Object> treeMap){

//        首先转移文件 然后添加原始文件 然后插入文件数据库
        if (treeMap.get("filePath")!=null){

        }

        return new ResultBuilder(StatusCode.SUCCESS);
    }

    @PostMapping("/transferFile")
    public ResultBuilder<Boolean> transfer(@RequestBody TreeMap<String,String> treeMap){
        FilesDTO filesDTO = new FilesDTO();
        UserDTO userDTO = new UserDTO(userService.getUser(SecurityUtils.getSubject().getPrincipal().toString()));
        filesDTO.setIsDir(Files.no_dir);
        filesDTO.setCreateDate(new Date());
        filesDTO.setSelfName(treeMap.get("name"));
        filesDTO.setParentName(treeMap.get("parentName"));
        Files files = filesDTO.ConvertFiles();
        files.setUser(userDTO.convertUsers());
        Boolean result = filesService.uploadFile(files, null, null);
        return new ResultBuilder<>(result, result ? StatusCode.SUCCESS : StatusCode.FALL);
    }


    @GetMapping("/fileList")
    public ResultBuilder<List<FilesDTO>> getFileList(String parentName, String path) {
        UserDTO userDTO = null;
        userDTO = new UserDTO(userService.getUser(SecurityUtils.getSubject().getPrincipal().toString()));
        List<FilesDTO> fileList = filesService.UserFile(parentName, userDTO.getUid());
        return new ResultBuilder<>(fileList, StatusCode.SUCCESS);
    }

    @GetMapping("/folderList")
    public ResultBuilder<List<FilesDTO>> folderList(String parentName, String path){
        UserDTO userDTO = null;
        userDTO = new UserDTO(userService.getUser(SecurityUtils.getSubject().getPrincipal().toString()));
        List<FilesDTO> fileList = filesService.UserFiles(parentName, userDTO.getUid(),true);
        return new ResultBuilder<>(fileList, StatusCode.SUCCESS);
    }

    @GetMapping("/type")
    public ResultBuilder<List<FilesDTO>> getFileListByType(String type) {
        UserDTO userDTO = new UserDTO(userService.getUser(SecurityUtils.getSubject().getPrincipal().toString()));
        List<FilesDTO> fileList = filesService.UserFile(type, userDTO.getUid());
        return new ResultBuilder<>(fileList, StatusCode.SUCCESS);
    }



    @PostMapping("/delete")
    public ResultBuilder<Boolean> deleteFileById(@RequestBody Map<String, String> map) {
        if (map != null && map.size() >= 1 && map.containsKey("fid")) {
            Long id = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString()).getUid();
            Long fid = Long.valueOf(map.get("fid"));
            Files files = filesService.getFilesById(Long.valueOf(map.get("fid"))).ConvertFiles();
            Boolean result = files.getIsDir() == Files.is_dir ?
                    filesService.deleteFolders(files.getParentName(), files.getSelfName(), fid, id) : filesService.deleteUserFiles(id, fid);
            return new ResultBuilder<>(result, result ? StatusCode.SUCCESS : StatusCode.FALL);
        }
        return new ResultBuilder<>(StatusCode.FALL);
    }

    @PostMapping("/rename")
    public ResultBuilder<Boolean> Rename(FilesDTO filesDTO) {
        Long id = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString()).getUid();
        Files files = filesDTO.ConvertFiles();
        filesService.RenameFile(files);
        return new ResultBuilder<>(StatusCode.SUCCESS);
    }

    @GetMapping("/getFile")
    public ResultBuilder<OriginFileDTO> Files(@RequestParam String pathName,@RequestParam String fileName) {
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        try {
            pathName=URLDecoder.decode(pathName,"UTF-8");
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResultBuilder<>(StatusCode.REQUEST_PARAM_ERROR);
        }
        return new ResultBuilder<>(new OriginFileDTO(filesService.findByParentNameAndAndUserAndAndSelfName(pathName, user, fileName)), StatusCode.SUCCESS);
    }

    @GetMapping("/getFileById")
    public ResultBuilder<OriginFileDTO> getFiles(String fid) {
        FilesDTO filesDTO = filesService.getFilesById(Long.valueOf(fid));
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        return new ResultBuilder<OriginFileDTO>(new OriginFileDTO(filesService.findByParentNameAndAndUserAndAndSelfName(filesDTO.getParentName(), user, filesDTO.getSelfName())), StatusCode.SUCCESS);
    }


    @GetMapping("/aria2Torrent")
    public ResultBuilder<Boolean> downloadTorrent(String fid) {
        FilesDTO filesDTO = filesService.getFilesById(Long.valueOf(fid));
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        OriginFile originFile=filesService.findByParentNameAndAndUserAndAndSelfName(filesDTO.getParentName(),user,filesDTO.getSelfName());
        return new ResultBuilder<Boolean>(aria2Service.downloadTorrent(String.format("download-%s",user.getUid()),Long.valueOf(fid),user,originFile), StatusCode.SUCCESS);
    }

    @GetMapping("/aria2DownloadList")
    public ResultBuilder<List<String>> downloadList(){
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        return new ResultBuilder<List<String>>(aria2Service.downloadList(String.format("downloadList-%s",user.getUid()),user),StatusCode.SUCCESS);
    }

    @GetMapping("/aria2Detail")
    public ResultBuilder<String> ariaMissionDetail(String gid){
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        return new ResultBuilder<>(aria2Service.Detail(String.format("detail-%s", user.getUid()),gid),StatusCode.SUCCESS);
    }

    @GetMapping("/StoppedList")
    public ResultBuilder<List<Aria2File>> StoppedList(){
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        return new ResultBuilder<>(aria2Service.StoppedList(String.format("detail-%s", user.getUid()),user),StatusCode.SUCCESS);
    }

    @GetMapping("/searchFiles")
    public ResultBuilder<List<FilesDTO>> searchFiles(String keyword){
        FilesDTO filesDTO=new FilesDTO();
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        filesDTO.setSelfName(keyword);
        return new ResultBuilder<>(filesService.searchFiles(filesDTO,user),StatusCode.SUCCESS);
    }

}
