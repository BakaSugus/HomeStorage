package cn.j.netstorage.Controller;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Service.FilesService;
import cn.j.netstorage.tool.FilesUtil;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import com.google.gson.Gson;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.HashMap;

@RestController
@RequestMapping("/Files/Share/")
public class DlnaController {



    @Autowired
    Environment environment;

    @Autowired
    private FilesService filesService;

    @Value("${ip}")
    private String ip;


    @GetMapping("/DLNA")
    @RequiresPermissions(value = {"投屏"})
    public ResultBuilder<Boolean> DLNA(String fid, String device) {
//        "AS-SMG970U1[DMR]"
        try {
            Socket socket = new Socket("127.0.0.1", 2888);
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            HashMap hashMap = new HashMap();

            Files files = filesService.findByFid(Long.valueOf(fid));
            hashMap.put("type", "Upnp");
            OriginFile originFile = files.getOriginFile();
            HardDiskDevice hardDiskDevice = FilesUtil.convert(originFile.getHardDiskDevice());
            String uri = "http://" +
                    ip +
                    ":" + environment.getProperty("local.server.port") + "/" +
                    hardDiskDevice.getCustomName()
                    + "/" +
                    originFile.getFileName();
            hashMap.put("uri", uri);
            hashMap.put("info", URLDecoder.decode(device, "utf-8"));
            pw.println(new Gson().toJson(hashMap));
            //关闭资源
            pw.close();
            is.close();
            socket.close();
            return new ResultBuilder<>(true, StatusCode.SUCCESS);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return new ResultBuilder<>(false, StatusCode.FALL);

    }
}
