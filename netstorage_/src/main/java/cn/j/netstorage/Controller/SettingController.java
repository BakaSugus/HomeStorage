package cn.j.netstorage.Controller;

import cn.j.netstorage.Entity.DTO.UserDTO;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.User.Role;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.Vo.UserVo;
import cn.j.netstorage.Service.FilesService;
import cn.j.netstorage.Service.HardDeviceService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.FilesUtil;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiresUser
@RequestMapping("/v1/admin/")
@RequiresRoles(value = {"admin"})
public class SettingController {
    @Autowired
    FilesService filesService;
    @Autowired
    UserService userService;

    @Autowired
    HardDeviceService hardDeviceService;

    @GetMapping("DiskStatus")
    @RequiresRoles(value = {"admin"})
    public ResultBuilder hardDiskDeviceResultBuilder() {
        return new ResultBuilder<>(hardDeviceService.getHardDevices(), StatusCode.SUCCESS);
    }

    @GetMapping("/hard_Devices")
    @RequiresRoles(value = {"admin"})
    public ResultBuilder hardDiskDevices() {
        return new ResultBuilder<>(hardDeviceService.getHardDevices(), StatusCode.SUCCESS);
    }

    @GetMapping("/users")
    @RequiresRoles(value = {"admin"})
    public ResultBuilder<List<UserDTO>> Users() {
        return new ResultBuilder<>(userService.getUsers(), StatusCode.SUCCESS);
    }

    @PostMapping("/addUser")
    @RequiresRoles(value = {"admin"})
    public ResultBuilder<Boolean> addUser(@RequestBody UserVo userVo){
        User user =new User();
        user.setEmailAccount(userVo.getEmail());
        user.setPassword(userVo.getPassword());
        user.setNickName(userVo.getNickName());
        Role role=new Role();
        role.setRid(Long.valueOf(userVo.getRid()));
        user.setRole(FilesUtil.convert(role));
        return new ResultBuilder<>(userService.Register(user), StatusCode.SUCCESS);
    }

    @PostMapping("/UpdateUserRoles")
    @RequiresRoles(value = {"admin"})
    public ResultBuilder UpdateUserRoles(@RequestBody TreeMap<String,Object> treeMap){
        Long id=Long.valueOf(treeMap.get("id").toString());
        ArrayList<Integer> objects= (ArrayList<Integer>) treeMap.get("rids");
        return new ResultBuilder(userService.changeUserPermission(id,objects),StatusCode.SUCCESS);
    }

    @GetMapping("/AlterUser")
    @RequiresRoles(value = {"admin"})
    public ResultBuilder<Boolean> alterUser() {
//        修个屁
        return new ResultBuilder<>(false,StatusCode.FALL);
    }

    @GetMapping("/getPermission")
    @RequiresRoles(value = {"admin"})
    public ResultBuilder getAllPermission(){
        return new ResultBuilder<>(userService.getAllPermission(),StatusCode.SUCCESS);
    }

    @GetMapping("/getRolePermission")
    @RequiresRoles(value = {"admin"})
    public ResultBuilder getRolePermission(String id){
        Role role=userService.role(Long.valueOf(id));
        return new ResultBuilder<>(userService.getPermission(role),StatusCode.SUCCESS);
    }


    @PostMapping("/UpdatePermission")
    @RequiresRoles(value = {"admin"})
    public ResultBuilder updatePermission(@RequestBody TreeMap<String,Object> map){
        Long id=Long.valueOf(map.get("id").toString());
        ArrayList<Integer> objects= (ArrayList<Integer>) map.get("permissions");
        return new ResultBuilder<>(userService.changePermission(id,objects),StatusCode.SUCCESS);
    }
    @GetMapping("/getRoles")
    @RequiresRoles(value = {"admin"})

    public ResultBuilder<List<Role>> Roles() {
        return new ResultBuilder<>(userService.roles(), StatusCode.SUCCESS);
    }

    @GetMapping("/hasRole")
    @RequiresRoles(value = {"admin"})
    public ResultBuilder hasRoles(String id){
        return new ResultBuilder(userService.getUser(Long.valueOf(id)).getRole(),StatusCode.SUCCESS);
    }

    @GetMapping("/AlterRole")
    @RequiresRoles(value = {"admin"})
    public ResultBuilder<Boolean> Roles(@RequestBody Role role) {
        return new ResultBuilder<>(userService.AlterRole(role), StatusCode.SUCCESS);
    }
}
