package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.User.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionMapper extends JpaRepository<Permission,Long> {
}
