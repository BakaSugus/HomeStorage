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


    public void Add(FilesVersionDTO filesVersionDTO){
        if (this.children==null){
            this.children=new ArrayList<>();
            this.children.add(filesVersionDTO);
        }else{
            this.children.add(filesVersionDTO);
        }
    }
}
