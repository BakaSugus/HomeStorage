package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Log.Log;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import jdk.internal.util.xml.impl.Input;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface UploadService {

//    Boolean common_upload(MultipartFile uploadFile, String storagePath, User user);

    Boolean common_upload_Folder(Files files);

    Boolean common_upload_Folder(String parentName,String selfName,User user);

    Boolean slice_upload(MultipartFile file, int size, String fileName, String dst, String storagePath, int currentIndex, User user );
//
//    Boolean exist_upload(String filePath, String selfName, String storagePath, User user);
//
//    Boolean merge_upload(String fileName, String diskPath, String storagePath, int start, int end, User user);

    Boolean checkMd5AndTransfer(String md5, String parentName, String selfName, User user);

    String getFinalFilesName(String folderName, String fileName, Type type, User user);

    Boolean multi_exist_upload(String taskName, String storagePath, String name, String filePath, User user);

    Boolean exist_upload(String filePath, String selfName, String storagePath, User user);

    Boolean merge_upload(String Driver,String fileName, String diskPath, String storagePath, int start, int end, User user);

    Boolean common_upload(MultipartFile uploadFile, String storagePath, User user);

    boolean common_upload(InputStream inputStream,String parent,String self,User user);

    String createAutoUploadPath(String token,String projectName,String desc);

    String getAutoUploadPath(String token,String projectName,boolean visible);

    Boolean AutoUploadComplete(String token, String projectName, List<File> files);

    Boolean AutoUploadComplete(String token , String projectName, File [] files);

    Boolean AutoUploadComplete(String token , String projectName);

    boolean uploadLog(User user, String filePath, String fileName, Log log,boolean visible);

    Boolean auto_upload(Type type,String folderName,String projectName, User user, File disk_file) ;

    Files getFinalName(User owner_user, User user, String storagePath, String fileName, Type type, OriginFile originFile);

}
