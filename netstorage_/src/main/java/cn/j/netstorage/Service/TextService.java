package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.User.User;

public interface TextService {

    int checkOriginFileUse(Files files, User user);

    boolean AppendLine(Files files,String content,User user);

    boolean CoverContent(Files file ,String content,User user);

    boolean setText(String content,String path,boolean append);

}
