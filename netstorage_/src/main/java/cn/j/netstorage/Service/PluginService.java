package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.Log.Log;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.plugin.Plugin;

import java.io.File;
import java.util.List;

public interface PluginService {

    boolean Mission(Files files ,String cmd);//添加任务 /xxx/bin/exe --name = sb

    boolean AriaPlugin();

    boolean FFmpegPlugin();

    boolean checkPlugin();

    boolean getPlugins();

    boolean setPluginLog(String PluginName);

    boolean Env();//Python,NodeJs,PHP

    boolean createLog(Folder folder, Log log);



}
