package cn.j.netstorage.Controller;

import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.Vo.UserVo;
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
//        String email, String password, Boolean rememberMe
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

    @GetMapping("testThread")
    public ResultBuilder<Boolean> test1(){
        Thread thread= new Thread(new Runnable() {
            /**
             * When an object implementing interface <code>Runnable</code> is used
             * to create a thread, starting the thread causes the object's
             * <code>run</code> method to be called in that separately executing
             * thread.
             * <p>
             * The general contract of the method <code>run</code> is that it may
             * take any action whatsoever.
             *
             * @see Thread#run()
             */
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    System.out.println("hello");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("中断");
                }
            }
        });
        thread.start();
        return new ResultBuilder<>(true,StatusCode.SUCCESS);
    }
}
