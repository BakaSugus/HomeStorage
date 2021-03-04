package cn.j.netstorage.Controller;

import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.Aria2Service;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import com.google.gson.JsonObject;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/download")
public class AriaController {
    @Autowired
    private Aria2Service aria2Service;
    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public String Active() {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return null;
        User user=userService.getUser(object.toString());
        JsonObject jsonObject=new JsonObject();
        jsonObject.add("active",aria2Service.getActive(user));
        jsonObject.add("stopped",aria2Service.getStopped(user));
        jsonObject.add("waited",aria2Service.getWaiting(user));
        return jsonObject.toString();
    }



    @GetMapping("/finish")
    public ResultBuilder Finish(String path, String name, String gid) {
        return null;
//        return new ResultBuilder<>(aria2Service.finish(gid,path),StatusCode.SUCCESS);
    }

    @PostMapping("/addUri")
    public ResultBuilder addURI(String url, String path) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder<>(aria2Service.download(url, path, userService.getUser(object.toString())), StatusCode.SUCCESS);
    }

    @PostMapping("/downloadTorrent")
    public ResultBuilder addTorrent(Long target, String path) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder<>(aria2Service.download(target, path, userService.getUser(object.toString())), StatusCode.SUCCESS);
    }

    @PostMapping("/stop")
    public ResultBuilder doStop(String gid) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder<>(aria2Service.stop(gid, userService.getUser(object.toString())), StatusCode.SUCCESS);
    }

    @PostMapping("/doCancel")
    public ResultBuilder doCancel(String gid) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder<>(aria2Service.cancel(gid, userService.getUser(object.toString())), StatusCode.SUCCESS);
    }

}
