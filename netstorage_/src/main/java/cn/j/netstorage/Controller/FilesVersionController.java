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
@RequestMapping("/FilesVersion")
@RequiresUser
public class FilesVersionController {
    @Autowired
    private UserService userService;
    @Autowired
    private FilesVersionService filesVersionService;
    @Autowired
    private FilesService filesService;


    @GetMapping("/uploadhistory")
    public ResultBuilder get(String path, String selfName) {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj == null)
            return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder<>(filesVersionService.get(path, selfName, userService.getUser(obj.toString())), StatusCode.SUCCESS);
    }
}
