package cn.j.netstorage.Entity.DTO;

import cn.j.netstorage.Entity.File.FilesVersion;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Setter
@Getter
@ToString
public class FilesVersionDTO {
    private Long GroupId;
    private String GroupName;
    private Double version;
    private Date updateTime;
    private List<FilesVersionDTO> children;
    private String desc_;

    public FilesVersionDTO(){

    }

    public FilesVersionDTO ConvertFileVersionDTO(FilesVersion version){
        FilesVersionDTO versionDTO=new FilesVersionDTO();
        versionDTO.desc_=version.getDesc_();
        versionDTO.GroupId=version.getGroupId();
        versionDTO.GroupName=version.getGroupName();
        versionDTO.updateTime=version.getUpdateDate();
        versionDTO.version=version.getVersion();
        return versionDTO;
    }

    public void Add(FilesVersionDTO filesVersionDTO){
        if (this.children==null){
            this.children=new ArrayList<>();
            this.children.add(filesVersionDTO);
        }else{
            this.children.add(filesVersionDTO);
        }
    }

    public FilesVersion ConvertFilesVersion(){
        FilesVersion filesVersion=new FilesVersion();
        filesVersion.setVersion(this.version);
        filesVersion.setGroupName(this.GroupName);
        filesVersion.setUpdateDate(this.updateTime);
        return filesVersion;
    }

    public void CheckChildren(){
        if (this.children==null){
            this.children= Collections.emptyList();
        }
    }
}
