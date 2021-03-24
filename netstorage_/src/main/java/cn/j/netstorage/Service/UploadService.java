package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

//    Boolean common_upload(MultipartFile uploadFile, String storagePath, User user);

    Boolean common_upload_Folder(Files files);

    Boolean slice_upload(MultipartFile file, int size, String fileName, String dst, String storagePath, int currentIndex, User user );
//
//    Boolean exist_upload(String filePath, String selfName, String storagePath, User user);
//
//    Boolean merge_upload(String fileName, String diskPath, String storagePath, int start, int end, User user);

    Boolean checkMd5AndTransfer(String md5, String parentName, String selfName, User user);

    String getFinalFilesName(String folderName, String fileName, Type type, User user);

    Boolean multi_exist_upload(String taskName, String storagePath, String name, String filePath, User user);


    Boolean exist_upload(String filePath, String selfName, String storagePath, User user);

    Boolean merge_upload(String fileName, String diskPath, String storagePath, int start, int end, User user);

    Boolean common_upload(MultipartFile uploadFile, String storagePath, User user);

}
