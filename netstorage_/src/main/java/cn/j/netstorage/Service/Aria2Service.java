package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.User.User;
import com.google.gson.JsonArray;

public interface Aria2Service {

    //去下载
    Boolean download(String url, String path, User user);

    //去下载种子
    Boolean download(Long fid, String path, User user);

    //去查列表
    JsonArray getWaiting(User user);

    JsonArray getStopped(User user);

    JsonArray getActive(User user);

    //暂停
    Boolean stop(String  id, User user);

    //继续
    Boolean start(String id, User user);

    //完成
    Boolean finish(String gid, String filePath);

    Boolean cancel(String gid,User user);

}
