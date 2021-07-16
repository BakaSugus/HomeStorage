package cn.j.netstorage.Controller;

import cn.j.netstorage.Service.DriverService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/driver")
public class DriverController {
    @Autowired
    private DriverService driverService;

    @Autowired
    private UserService userService;

    @GetMapping("/driver")
    public ResultBuilder driver() {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj == null)
            return new ResultBuilder(StatusCode.FALL);
        List<String> list = driverService.getDriver(userService.getUser(obj.toString()));
        return new ResultBuilder<>(list,StatusCode.SUCCESS);
    }
}
