package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.User.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleMapper extends JpaRepository<Role,Long> {
    Role findByName(String name);
}
