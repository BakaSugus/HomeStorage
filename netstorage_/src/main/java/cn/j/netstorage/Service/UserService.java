package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.DTO.UserDTO;
import cn.j.netstorage.Entity.Token;
import cn.j.netstorage.Entity.User.Permission;
import cn.j.netstorage.Entity.User.Role;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.Vo.UserVo;

import java.util.List;
import java.util.Set;

public interface UserService {

    Boolean Login(String Email, String password, Boolean rememberMe);

    Boolean Register(User user);

    Boolean freezeUser(User user);

    User getUser(Object token);

    User getUser(Token token);

    User getUser(Long id);

    User getUser(String account, String password);

    List<UserDTO> getUsers();

    Set<Permission> getPermission(Role role);

    List<Permission> getAllPermission();

    Set<Role> getRole(Object token);

    Boolean changePermission(Long id, List<Integer> pids);

    Boolean changeUserPermission(Long id, List<Integer> pids);

    Role role(Long id);

    Boolean addRole(Role role);

    List<Role> roles();

    List<Role> roles(List<Integer> rids);

    Long savePermission(Permission permission);

    Long saveUser(User user);

    Boolean AlterRole(Role role);

    List<UserDTO> search(UserVo userVo);
}
