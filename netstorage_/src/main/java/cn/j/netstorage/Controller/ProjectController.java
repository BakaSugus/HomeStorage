package cn.j.netstorage.Controller;

import cn.j.netstorage.Service.FileService2;
import cn.j.netstorage.Service.UploadService;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project")

public class ProjectController {

//
    @Autowired
    private UploadService uploadService;

    @Autowired
    private FileService2 fileService2;


    @PostMapping("createProject")
    public ResultBuilder createProject(String token,String projectName,String projectDesc) {
        if (token==null|| StringUtils.isEmpty(token))return new ResultBuilder(StatusCode.FALL);

        if (projectName==null||StringUtils.isEmpty(projectName))return new ResultBuilder(StatusCode.FALL);

        String result = uploadService.getAutoUploadPath(token,projectName,true);

        return new ResultBuilder<>(result,StatusCode.SUCCESS);
    }

    @GetMapping("/CallBack")
    public ResultBuilder callback(String projectName,String token) {
        if (token==null|| StringUtils.isEmpty(token))return new ResultBuilder(StatusCode.FALL);

        if (projectName==null||StringUtils.isEmpty(projectName))return new ResultBuilder(StatusCode.FALL);

        boolean res = uploadService.AutoUploadComplete(token,projectName);
        if (res)return new ResultBuilder(StatusCode.SUCCESS);

        return new ResultBuilder(StatusCode.FALL);
    }

    @GetMapping("/getProjectPath")
    public ResultBuilder ProjectPath(String projectName,String token) {
        if (token==null|| StringUtils.isEmpty(token))return new ResultBuilder(StatusCode.FALL);

        if (projectName==null||StringUtils.isEmpty(projectName))return new ResultBuilder(StatusCode.FALL);

        String result = uploadService.getAutoUploadPath(token,projectName,true);
        return new ResultBuilder<>(result,StatusCode.SUCCESS);
    }

    @PostMapping("/Log")
    public ResultBuilder Log(String projectName,String token,String logContent) {
        if (token==null|| StringUtils.isEmpty(token))return new ResultBuilder(StatusCode.FALL);

        if (projectName==null||StringUtils.isEmpty(projectName))return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder(StatusCode.SUCCESS);
    }
}
