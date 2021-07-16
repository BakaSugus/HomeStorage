package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.Folder.Folder;

public interface FFmpegService {

    boolean convertVideo(String fullName,String targetExt);

    boolean splitMusic(String fullName, String parentPath);

}
