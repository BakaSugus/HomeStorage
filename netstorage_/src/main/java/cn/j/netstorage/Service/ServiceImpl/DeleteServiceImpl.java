package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.DTO.DeleteDTOs;
import cn.j.netstorage.Entity.File.DeleteFile;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Mapper.DeleteMapper;
import cn.j.netstorage.Mapper.FileMapper;
import cn.j.netstorage.Service.DeleteService;
import cn.j.netstorage.Service.FileService2;
import cn.j.netstorage.Service.FilesService;
import cn.j.netstorage.Service.OriginFileService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DeleteServiceImpl implements DeleteService {

    @Autowired
    private DeleteMapper deleteMapper;
    @Autowired
    private FileService2 fileService2;
    @Autowired
    private FilesService filesService;
    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private OriginFileService originFileService;

    @Override
    public DeleteDTOs deletes(User user) {
        List<DeleteFile> files = deleteMapper.findAllByUserIs(user);
        return new DeleteDTOs(files);
    }

    @Override
    public DeleteDTOs deletes(User user, Long... id) {
        if (id == null || id.length == 0)
            return null;
        if (user == null)
            return null;
        List<DeleteFile> files = deleteMapper.findAllByUserIsAndIdIn(user, id);
        return new DeleteDTOs(files);
    }

    @Override
    public DeleteDTOs.DeleteDTO delete(Long id, User user) {
        if (id == null || id == 0)
            return null;
        if (user == null)
            return null;
        DeleteFile file = deleteMapper.getOne(id);
        if (!file.getUser().getEmailAccount().equals(user.getEmailAccount()))
            return null;
        return new DeleteDTOs.DeleteDTO(file);
    }

    @Override
    public Boolean DeleteFolders(User user, @NonNull Long... id) {
        return null;
    }

    @Override
    public Boolean restoreFolders(User user, @NonNull Long... id) {
        return null;
    }

    @Override
    public Boolean restoreFiles(User user, @NonNull Long... id) {
        List<DeleteFile> files = deleteMapper.findAllById(Arrays.asList(id));

        List<Files> filesList = new ArrayList<>();
        for (DeleteFile file : files) {
            if (file.getType().equals(Type.Folder.getType())) {
                filesList.add(file.toFiles());
            }
        }
        deleteMapper.deleteAll(files);
        fileMapper.saveAll(filesList);
        return true;
    }

    @Override
    public Boolean DeleteFiles(User user, @NonNull Long... id) {
        List<Files> files = null;
        if (id.length == 0) {
            return false;
        } else {
            files = fileService2.files(id);
            fileService2.del(files);
            List<DeleteFile> deleteFiles = new ArrayList<>();
            for (Files file : files) {
                if (!file.getType().equals(Type.Folder.getType()) && user.getEmailAccount().equals(file.getUser().get(0).getEmailAccount())) {
                    deleteFiles.add(new DeleteFile(file));
                }
            }
            deleteMapper.saveAll(deleteFiles);
        }
        return true;
    }

    @Override
    public Boolean DeleteFilesInRecycleBin(User user, Long... id) {

        Boolean res = false;
        List<DeleteFile> files = this.deleteMapper.findAllById(Arrays.asList(id));
        System.out.println(files.size());
        List<OriginFile> originFiles = new ArrayList<>();
        for (DeleteFile file : files) {
            int count = fileMapper.countAllByOriginFile(file.getOriginFile());
            if (count == 0)
                originFiles.add(file.getOriginFile());
        }
        deleteMapper.deleteAll(files);
        res = originFileService.deleteOriginFile(originFiles);
        return res;
    }

    @Override
    public Boolean loopDeleteFiles() {
        return null;
    }
}
