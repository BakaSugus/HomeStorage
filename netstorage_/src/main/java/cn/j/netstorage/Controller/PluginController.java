package cn.j.netstorage.Controller;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.DTO.UserDTO;
import cn.j.netstorage.Entity.Token;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.plugin.Plugin;
import cn.j.netstorage.Service.FilesService;
import cn.j.netstorage.Service.PluginService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/plugins")
public class PluginController {
    @Autowired
    UserService userService;
    @Autowired
    FilesService filesService;

    @GetMapping("/folderList")
    public ResultBuilder<List<FilesDTO>> folderList(String parentName, String token) {
        User user = userService.getUser(new Token(token));
        List<FilesDTO> fileList = filesService.UserFiles(parentName, user.getUid(), true);
        return new ResultBuilder<>(fileList, StatusCode.SUCCESS);
    }

    @GetMapping("/fileList")
    public ResultBuilder<List<FilesDTO>> fileList(String parentName, String token) {
        User user = userService.getUser(new Token(token));
        List<FilesDTO> fileList = filesService.UserFiles(parentName, user.getUid(), false);
        return new ResultBuilder<>(fileList, StatusCode.SUCCESS);
    }

    @GetMapping("/list")
    public ResultBuilder<List<FilesDTO>> list(String parentName, String token) {
        User user = userService.getUser(new Token(token));
        List<FilesDTO> fileList = filesService.UserFile(parentName, user.getUid());
        return new ResultBuilder<>(fileList, StatusCode.SUCCESS);
    }

    //将已有的文件转移到网盘内
    @GetMapping("/transferFile")
    public ResultBuilder<Boolean> insertExistFile(String file, String fileName) {
        if (!StringUtils.hasText(file))
            return new ResultBuilder<>(false, StatusCode.FALL);

        if (!StringUtils.hasText(fileName))
            return new ResultBuilder<>(false, StatusCode.FALL);

        return new ResultBuilder<>(StatusCode.SUCCESS);
    }

    @GetMapping("/moveFile")
    public ResultBuilder<Boolean> move(Long id, String parentName) {
        return new ResultBuilder<>(StatusCode.SUCCESS);
    }

    @GetMapping("/del")
    public ResultBuilder<Boolean> delete(String file) {
        return new ResultBuilder<>(StatusCode.SUCCESS);
    }

    //获得网盘的文件在磁盘的位置 id

    //获得随机获得一个磁盘位置

//    @Autowired
//    private PluginService pluginService;
//
//    @GetMapping("/plugins")
//    public ResultBuilder<List<Plugin>> getPlugins() {
//        return new ResultBuilder<>(pluginService.httpPlugins(), StatusCode.SUCCESS);
//    }
//
//    @GetMapping("/plugin")
//    public ResultBuilder<Plugin> getPlugin(String id) {
//        return new ResultBuilder<>(pluginService.httpPlugin(Long.valueOf(id)), StatusCode.SUCCESS);
//    }
//
//    @PostMapping("/plugin")
//    public ResultBuilder<Boolean> savePlugin(@RequestBody Plugin httpPlugin) {
//        return new ResultBuilder<>(pluginService.save(httpPlugin), StatusCode.SUCCESS);
//    }
//
//    @PostMapping("/deletePlugin")
//    public ResultBuilder<Boolean> deletePlugin(@RequestBody Plugin httpPlugin) {
//        return new ResultBuilder<>(pluginService.delete(httpPlugin), StatusCode.SUCCESS);
//    }
//
//    @GetMapping("search")
//    public ResultBuilder<List<Plugin>> search(@RequestBody Plugin httpPlugin) {
//        return new ResultBuilder<>(pluginService.httpPlugins(), StatusCode.SUCCESS);
//    }
}
