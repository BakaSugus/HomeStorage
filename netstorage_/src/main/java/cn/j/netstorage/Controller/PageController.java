package cn.j.netstorage.Controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiresUser
public class PageController {

    @GetMapping("/storage")

    public String indexAliases() {
        return "index.html";
    }

    @GetMapping("/User/login")
    public String login() {
        return "index.html";
    }

    @GetMapping("/User/register")
    public String register() {
        return "index.html";
    }

    @GetMapping("/video")
    public String video() {
        return "index.html";
    }

    @GetMapping("/storage/Admin")
    @RequiresRoles("admin")
    public String admin() {
        return "index.html";
    }
}
