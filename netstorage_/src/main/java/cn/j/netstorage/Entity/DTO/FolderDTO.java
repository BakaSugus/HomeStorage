package cn.j.netstorage.Entity.DTO;


import cn.j.netstorage.Entity.Folder.Folder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
@Setter
@Getter
@ToString
public class FolderDTO extends BaseDTO{
    protected Long id;

    protected String name;
    private String OriginUserName;
    private String shareUserName;


    private FilesDTO files;
    public FolderDTO(){}

    public FolderDTO(Folder folder){
        this.id=folder.getId();
        this.name=folder.getFolderName();
        this.files=new FilesDTO(folder.getFolder());
        this.OriginUserName=folder.getOriginUser().getNickName();
        if (folder.getShareUser().size()!=0) this.shareUserName=folder.getShareUser().iterator().next().getNickName();
    }
}
