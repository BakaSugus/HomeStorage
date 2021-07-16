package cn.j.netstorage.Controller;

import cn.j.netstorage.Entity.DTO.DeleteDTOs;
import cn.j.netstorage.Service.DeleteService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 回收站和删除到回收站和回收站删除
 */
@RestController
@RequiresUser
@RequestMapping("/Deletes/")
public class DeleteController {


    @Autowired
    private DeleteService deleteService;
    @Autowired
    private UserService userService;

    /**
     * 获得该用户回收站的文件
     *
     * @return
     */
    @GetMapping("/Deletes")
    public ResultBuilder deletes() {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        DeleteDTOs deleteDTOs = deleteService.deletes(userService.getUser(object.toString()));
        return new ResultBuilder(deleteDTOs, StatusCode.SUCCESS);
    }

    /**
     * 获得某个回收站文件
     *
     * @return
     */
    @GetMapping("/DeleteFile")
    public ResultBuilder delete(Long id) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        DeleteDTOs.DeleteDTO file = deleteService.delete(id, userService.getUser(object.toString()));
        return new ResultBuilder(StatusCode.SUCCESS);
    }

    /**
     * 删除到回收站
     *
     * @return
     */
    @PutMapping("/Delete")
    public ResultBuilder addDelete(String driver, String ... id) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        Boolean res = deleteService.DeleteFiles(driver, userService.getUser(object.toString()), id);
        return new ResultBuilder<>(res, StatusCode.SUCCESS);
    }

    /**
     * 回收站删除该文件
     *
     * @param id
     * @return
     */
    @DeleteMapping("/Delete")
    public ResultBuilder deleteInBin(Long id) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        deleteService.DeleteFilesInRecycleBin(userService.getUser(object.toString()), id);
        return new ResultBuilder(StatusCode.SUCCESS);
    }

    /**
     * 回收站还原文件
     *
     * @param id
     * @return
     */
    @PostMapping("/Restore")
    public ResultBuilder restore(Long id) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        Boolean res = deleteService.restoreFiles(userService.getUser(object.toString()), id);
        return new ResultBuilder(res, StatusCode.SUCCESS);
    }
}
