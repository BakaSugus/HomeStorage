package cn.j.netstorage.Controller;

import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.Vo.UserVo;
import cn.j.netstorage.Service.OriginFileService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/User")
public class HelloController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResultBuilder<Boolean> login(HttpServletResponse response, @RequestBody UserVo user) {
        Boolean result = userService.Login(user.getEmail(), user.getPassword(), user.getRememberMe());
        if (result) {
            Cookie cookie = new Cookie("nickName", userService.getUser(user.getEmail()).getNickName());
            cookie.setMaxAge(15*86400);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        return new ResultBuilder<>(result, StatusCode.SUCCESS);
    }

    @PostMapping("/register")
    public ResultBuilder<Boolean> register(@RequestBody HashMap<String, String> hashMap) {
        User user = new User();
        return new ResultBuilder<>(userService.Register(user), StatusCode.SUCCESS);
    }

}
