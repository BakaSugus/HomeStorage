package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.plugin.Plugin;

import java.io.File;
import java.util.List;

public interface PluginService {

    Boolean transferFiles(String file, String fileName, Long id, String diskName, User user);

    Boolean transferFiles(List<File> files);

    Boolean pyControl();//Jython
}
