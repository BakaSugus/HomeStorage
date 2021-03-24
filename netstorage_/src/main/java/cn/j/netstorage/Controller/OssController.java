package cn.j.netstorage.Controller;

import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.oss.Oss;
import cn.j.netstorage.Service.OssService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Oss")
public class OssController {
    @Autowired
    private OssService tencent;
    @Autowired
    private UserService userService;

    @GetMapping("/Oss")
    public ResultBuilder getBuckets(){
        Object object=SecurityUtils.getSubject().getPrincipal();
        if (object==null)
            return new ResultBuilder<>(StatusCode.FALL);
        return new ResultBuilder<>(tencent.getAllBucket(userService.getUser(object.toString())),StatusCode.SUCCESS);
    }

    @GetMapping("/OssFiles")
    public ResultBuilder getBucketsFiles(String buckName,String path){
        Object object=SecurityUtils.getSubject().getPrincipal();
        if (object==null)
            return new ResultBuilder<>(StatusCode.FALL);
        return new ResultBuilder<>(tencent.get(userService.getUser(object.toString()),buckName,path),StatusCode.SUCCESS);
    }

    @PutMapping("/Oss")
    public ResultBuilder add(Oss oss){
        Object object=SecurityUtils.getSubject().getPrincipal();
        if (object==null)
            return new ResultBuilder<>(StatusCode.FALL);
        return new ResultBuilder<>(tencent.add(oss,userService.getUser(object.toString())),StatusCode.SUCCESS);
    }
}
