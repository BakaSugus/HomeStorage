package cn.j.netstorage.Entity.DTO;


import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.Folder.FolderPermission;
import cn.j.netstorage.Entity.User.Permission;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;
@Setter
@Getter
@ToString
public class FolderDTO extends BaseDTO{
    protected Long id;

    protected String name;
    private String OriginUserName;
    private String shareUserName;

    private Set<FolderPermission> permissions;

    private FilesDTO files;
    public FolderDTO(){}

    public FolderDTO(Folder folder){
        this.id=folder.getId();
        this.name=folder.getFolderName();
        this.files=new FilesDTO(folder.getFolder());
        this.OriginUserName=folder.getOriginUser().getNickName();
        this.shareUserName=folder.getShareUser().getNickName();
        this.permissions=folder.getPermissions();
    }
}
