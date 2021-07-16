package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.Driver.Driver;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.VisitRecord;
import cn.j.netstorage.Mapper.FileMapper;
import cn.j.netstorage.Mapper.FolderMapper;
import cn.j.netstorage.Mapper.RecordMapper;
import cn.j.netstorage.Service.DriverService;
import cn.j.netstorage.Service.FileService2;
import cn.j.netstorage.Service.FolderService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.FilesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Service
public class FilesServiceImpl implements FileService2 {

    @Autowired
    FileMapper fileMapper;

    @Autowired
    UserService userService;

    @Autowired
    FolderMapper folderMapper;
    @Autowired
    FolderService folderService;
    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    DriverService driverService;

    @Override
    public Boolean save(Files files) {
        return fileMapper.save(files).getFid() != 0;
    }

    @Override
    public Files saveAndGet(Files files) {
        return fileMapper.save(files);
    }

    @Override
    public Boolean del(Files files) {
        try {
            fileMapper.deleteById(files.getFid());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean del(List<Files> files) {
        files.removeIf(file -> Type.Folder.getType().equals(file.getType()));
        try {
            fileMapper.deleteAll(files);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<Files> get(String path, String name, User user) {
        return fileMapper.findAllBySelfNameAndUserAndParentName(name, user, path);
    }

    @Override
    public Boolean move(Files files, String path) {
        return null;
    }

    @Override
    public List<Files> files(Long... id) {
        return fileMapper.findAllById(Arrays.asList(id));
    }

    @Override
    public List<Files> files() {
        return null;
    }

    @Override
    public Files file(String finalName, OriginFile originFile, String storagePath, User user) {
        Files files = new Files();
        files.setParentName(storagePath);
        files.setUser(Collections.singletonList(user));
        files.setCreateDate(new Date());
        files.setOriginFile(Collections.singleton(originFile));
        files.setSelfName(finalName);

        Type type = Type.getInstance(FilesUtil.getExt(finalName));
        files.setType(type.getType());

        return files;
    }

    @Override
    public int checkFilesCount(String parentName, String fileName, User user) {
        int pos = fileName.lastIndexOf(".");
        if (pos == -1)
            pos = fileName.length();
        String likeName = fileName.substring(0, pos) + "%" + fileName.substring(pos);
        return fileMapper.findAllBySelfNameLikeAndUserAndParentName(likeName, user, parentName).size();
    }


    public List<FilesDTO> folders(User user) {
        List<FilesDTO> filesDto = new ArrayList<>();
        List<Folder> list = folderMapper.findByShareUser(user);
        for (Folder f :
                list) {
            filesDto.add(new FilesDTO(f.getFolder()));
        }
        return filesDto;
    }

    public Boolean deleteFolders(Long shareId) {
        try {
            folderMapper.deleteById(shareId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean RenameFile(User user, long fid, String targetName) {
        List<Files> filesList = files(fid);
        Files files = null;
        if (filesList == null || filesList.size() < 1) {
            return false;
        }

        files = filesList.get(0);
        files.setSelfName(targetName);
        return save(files);
    }

    @Override
    public Boolean moveFiles(User user, long fid, long targetFid) {
        Folder folder = folderService.folder(targetFid);
        if (folder == null) return false;
        String parentName = folder.getFolderName();
        Files file = fileMapper.findById(fid).get();
        if (file == null || file.getFid() == 0) return false;
        file.setParentName(parentName);
        return save(file);
    }

    @Override
    public void zip(ZipOutputStream zipOutputStream, User user, Long... fid) {
        List<Files> files = files(fid);
        try {
            for (int i = 0; i < files.size(); i++) {
                Files f = files.get(i);
                if (f.getType().equals(Type.Folder)) {
                    //是文件夹
                    zipEntry(f.getParentName() + f.getSelfName() + "/", f, zipOutputStream);
                } else {
                    List<OriginFile> originFiles = Collections.singletonList((f.getOriginFile()));
                    File file = new File(originFiles.get(originFiles.size() - 1).getPath());
                    FileInputStream fis = new FileInputStream(file);

                    zipOutputStream.putNextEntry(new ZipEntry(file.getName()));

                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zipOutputStream.write(bytes, 0, length);
                    }
                    fis.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }


    @Override
    public boolean addVisitRecord(User user, Files files) {
        return false;
    }

    @Override
    public Files getFiles(String path, String selfName, User user) {
        List<Files> files = fileMapper.findAllBySelfNameAndUserAndParentName(selfName, user, path);
        if (files == null || files.size() == 0) return null;
        return files.get(0);
    }

    @Override
    public boolean saveRecord(VisitRecord visitRecord) {
        return recordMapper.save(visitRecord).getId() != 0;
    }

    @Override
    public String checkName(String storagePath, String OriginalFilename, User user) {
        int count = checkFilesCount(storagePath, OriginalFilename, user);
        String finalName = String.format("%s%s%s",
                OriginalFilename.substring(0, OriginalFilename.lastIndexOf(".")),
                count == 0 ? "" : "(" + count + ")",
                OriginalFilename.substring(OriginalFilename.lastIndexOf("."))
        );
        return finalName;
    }

    public void zipEntry(String location, Files files, ZipOutputStream zipOutputStream) {
        //此处查是不是共享文件夹
        Folder folder = folderService.folders(files);
        User user = folder == null || folder.getOriginUser() == null ?
                files.getUser().get(0) : folder.getOriginUser();
        //此处查子文件和子文件夹
        List<Files> anySonFiles = fileMapper.findByParentNameLikeAndUser(files.getParentName() + files.getSelfName() + "/", user);
        for (int i = 0; i < anySonFiles.size(); i++) {
            Files f = anySonFiles.get(i);
            if (f.getType().equals(Type.Folder.getType())) {
                continue;
            }
            List<OriginFile> originFiles = Collections.singletonList(f.getOriginFile());
            try (FileInputStream fis = new FileInputStream(new File(originFiles.get(originFiles.size() - 1).getPath()).getAbsolutePath());) {
                String name = f.getParentName() + f.getSelfName();
                ZipEntry zipEntry = new ZipEntry("/" + name.replace(location, ""));
                zipOutputStream.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOutputStream.write(bytes, 0, length);
                }
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public HashMap<String, String> getOriginFileAttruite(Files files) {
        OriginFile originFile = files.getOriginFile();
        if (originFile == null)
            return new HashMap<>();
        String type = files.getType();
        return new HashMap<>();
    }

    public List<String> getZipFileList(Files files) {
        OriginFile originFile = files.getOriginFile();
        String filePath = "C:\\Users\\Shinelon\\Downloads\\新建文件夹 (2).zip";
        List<String> list = new ArrayList<>();
        if (filePath == null || StringUtils.isEmpty(filePath) || !filePath.endsWith(".zip"))
            return list;

        try {
            ZipFile zfile = new ZipFile(filePath);
            Enumeration zList = zfile.entries();
            ZipEntry ze = null;
            while (zList.hasMoreElements()) {
                ze = (ZipEntry) zList.nextElement();
                list.add(ze.getName());
            }
            zfile.close();
        } catch (IOException e) {
            e.printStackTrace();
            return list;
        }
        return list;
    }
}
