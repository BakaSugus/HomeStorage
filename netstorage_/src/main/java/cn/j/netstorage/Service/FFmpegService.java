package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.Folder.Folder;

public interface FFmpegService {

    boolean convertVideo(Files files);

    boolean splitMusic(Files files);

}
