package cn.j.netstorage.Controller;

import cn.j.netstorage.Entity.DTO.FilesVersionDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.FilesVersion;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.FilesService;
import cn.j.netstorage.Service.FilesVersionService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import com.google.gson.Gson;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/v1/FilesVersion")
@RequiresUser
public class FilesVersionController {
    @Autowired
    private UserService userService;
    @Autowired
    private FilesVersionService filesVersionService;
    @Autowired
    private FilesService filesService;

    @GetMapping("/FileVersionControlList")
    public ResultBuilder<List<FilesVersionDTO>> listFilesVersion() {
        Long id = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString()).getUid();
        return new ResultBuilder<>(filesVersionService.filesVersionList(id), StatusCode.SUCCESS);
    }

    @GetMapping("/FileVersion")
    public ResultBuilder getOneFileVersion(String GroupName){
        User user= userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        return new ResultBuilder<List<FilesVersionDTO>>(filesVersionService.GetFileVersionByGroupName(user,GroupName),StatusCode.SUCCESS);
    }

    @PostMapping("/updateVersion")
    public ResultBuilder<Boolean> updateVersion(@RequestParam("upload") MultipartFile multipartFile, FilesVersion filesVersion) {
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        filesVersion.setUsers(Collections.singleton(user));
        System.out.println(filesVersion.getDesc_());
        return new ResultBuilder<>(filesVersionService.add(filesVersion, multipartFile), StatusCode.SUCCESS);
    }

    @PostMapping("/delete")
    public ResultBuilder<Boolean> deleteFilesVersionControl(@RequestBody TreeMap<String,String> map) {
        if (map.containsKey("groupId")){
            Long id = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString()).getUid();
            FilesVersion filesVersion = filesVersionService.GetFileVersionByGroupId(Long.valueOf(map.get("groupId")));
            for (User user :
                    filesVersion.getUsers()) {
                if (user.getUid() == id)
                    return new ResultBuilder<>(filesVersionService.delete(filesVersion), StatusCode.SUCCESS);
            }
        }
        return new ResultBuilder<>(false, StatusCode.FALL);
    }

    @PostMapping("/insertFilesVersionControl")
    public ResultBuilder<Boolean> insertFilesVersionControl(@RequestBody String fid) {
        String id = new Gson().fromJson(fid, HashMap.class).get("fid").toString();
        Boolean result = false;
        if (StringUtils.isEmpty(id)) {
            return new ResultBuilder<>(result, StatusCode.FALL);
        }
        Files files = filesService.findByFid(Long.valueOf(id));

        FilesVersion filesVersion = new FilesVersion();
        User user = userService.getUser(SecurityUtils.getSubject().getPrincipal().toString());
        OriginFile originFile = files.getOriginFile().size() != 0 ?
                new ArrayList<OriginFile>(files.getOriginFile()).get(0) : null;

        if (originFile == null) {
            return new ResultBuilder<>(result, StatusCode.FALL);
        }
        if (files.getOriginFile() != null && files.getOriginFile().size() > 0) {
            filesVersion.setOriginFileSet(Collections.singleton(originFile));
            filesVersion.setDesc_("默认版本描述");
            filesVersion.setGroupName(files.getParentName() + files.getSelfName());
            filesVersion.setVersion(1d);
            filesVersion.setUpdateDate(new Date());
            filesVersion.setUsers(Collections.singleton(user));
            return new ResultBuilder<>(filesVersionService.createFileVersionControl(filesVersion), StatusCode.SUCCESS);
        }
        return new ResultBuilder<>(result, StatusCode.FALL);
    }
}
