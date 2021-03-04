package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.User.Role;

import java.util.List;

public interface RoleService {

    Role role(Long id);

    Boolean addRole(Role role);

    Boolean updateUserRole();


}
