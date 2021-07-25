package cn.j.netstorage.Entity.DTO;

import cn.j.netstorage.Entity.Driver.Driver;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DriverDTO {
    private long id;
    private String DriverName;

    public DriverDTO(Driver driver) {
        this.id = driver.getId();
        this.DriverName = driver.getBucketName();
    }
}
