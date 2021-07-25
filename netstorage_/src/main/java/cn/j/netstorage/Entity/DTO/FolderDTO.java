package cn.j.netstorage.Entity.DTO;


import cn.j.netstorage.Entity.Driver.Driver;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@ToString
public class FolderDTO extends FilesDTO {
    private boolean Filing;
    private boolean Inherit;
    private boolean Share;
    private Set<DriverDTO> drivers;
    private Set<UserDTO> shareUser;
    public FolderDTO() {
    }

    public FolderDTO(Folder folder) {
        super(folder.getFolder());
        this.Filing = folder.isFiling();
        this.Inherit = folder.isInherit();
        this.Share = folder.isShare();

        Set<Driver> drivers = folder.getDrivers();
        this.drivers = new HashSet<>();

        for (Driver driver : drivers) {
            this.drivers.add(new DriverDTO(driver));
        }

        Set<User> user = folder.getShareUser();
        shareUser = new HashSet<>();
        for (User usr : user) {
            shareUser.add(new UserDTO(usr));
        }
    }
}
