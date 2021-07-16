package cn.j.netstorage.Entity.DTO;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.aspectj.weaver.ast.Or;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Setter
@Getter
@ToString
public class FilesDTO {
    private String fid;
    private String selfName;
    private String parentName;
    private Date createDate;
    private Long Size;
    private String type;

    public FilesDTO() {
    }

    public FilesDTO(String fid, String selfName, String parentName, short isDir, String type) {
        this.fid = fid;
        this.selfName = selfName;
        this.parentName = parentName;
        this.type = type;
    }

    public FilesDTO(Files files) {
        this.fid = String.valueOf(files.getFid());
        this.parentName = files.getParentName();
        this.createDate = files.getCreateDate();
        this.selfName = files.getSelfName();
        OriginFile originFile = files.getOriginFile() != null ? files.getOriginFile() : new OriginFile();
        this.Size = originFile.getSize() == null ? 0 : originFile.getSize();
        this.type = files.getType();
        originFile = null;
    }

    public FilesDTO(String fid, String selfName, String parentName, short isDir, Long Size, String type) {
        this.fid = fid;
        this.selfName = selfName;
        this.parentName = parentName;
        this.type = type;
        this.Size = Size;
    }

    public FilesDTO(String fid, String selfName, String parentName, short isDir, Long Size, String type,Date lastDate) {
        this.fid = fid;
        this.selfName = selfName;
        this.parentName = parentName;
        this.type = type;
        this.Size = Size;
        if (lastDate!=null) this.createDate=lastDate;
    }

    public FilesDTO(Folder folder) {
        this(folder.getFolder());
    }

    public Files ConvertFiles() {
        return ConvertFiles(this);
    }

    private Files ConvertFiles(FilesDTO filesDTO) {
        Files files = new Files();
        return ConvertFiles(files, filesDTO);
    }

    private Files ConvertFiles(Files files, FilesDTO filesDTO) {
        files.setSelfName(filesDTO.selfName);
        files.setParentName(filesDTO.parentName);
        return files;
    }


}
