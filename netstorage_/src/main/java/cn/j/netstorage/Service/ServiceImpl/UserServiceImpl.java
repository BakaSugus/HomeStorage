package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.DTO.UserDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.User.Permission;
import cn.j.netstorage.Entity.User.Role;
import cn.j.netstorage.Entity.User.Token;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.Vo.UserVo;
import cn.j.netstorage.Mapper.*;
import cn.j.netstorage.Service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    PermissionMapper permissionMapper;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    TokenMapper tokenMapper;

    @Autowired
    FileMapper fileMapper;

    /**
     * 登陆，在这里调用ShiroRealm
     *
     * @param Email      邮箱
     * @param password   密码
     * @param rememberMe 记住我
     * @return 登陆结果
     */
    @Override
    public Boolean Login(String Email, String password, Boolean rememberMe) {
        Subject subject = SecurityUtils.getSubject();
        //生成token
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(
                Email, password, rememberMe);
        try {
            subject.login(usernamePasswordToken);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 注册,写数据库
     *
     * @param user 用户信息 最低需要邮箱 密码 nickName
     * @return 注册结果
     */
    @Override
    public Boolean Register(User user) {
        user.Md5Hash();
        user.setCreateDate(System.currentTimeMillis());
        user = userMapper.save(user);
        if (user.getUid() != 0) {
            Token token = new Token(user);
            token = tokenMapper.save(token);

            return token.getId() != 0 && user.getUid() != 0;
        }
        return false;
    }

    @Override
    public Boolean freezeUser(User user) {
        return null;
    }

    /**
     * 根据token获得用户信息
     *
     * @param token Shiro的token
     * @return 用户
     */
    @Override
    public User getUser(Object token) {
        User user = new User();
        //token是邮箱
        user.setEmailAccount(token.toString());
        Example<User> example = Example.of(user, ExampleMatcher.matching().withIgnorePaths("uid", "create_date", "nick_name", "role", "password"));
        Optional<User> list = userMapper.findOne(example);
        return list.orElse(null);
    }

    @Override
    public User getUser(Token token) {
        return tokenMapper.findByToken(token.getToken()).getUser();
    }

    public User getUser(Long id) {
        return userMapper.findById(id).get();
    }

    @Override
    public User getUser(String account, String password) {
        User user = new User();
        user.setPassword(password);
        user.setEmailAccount(account);
        Example<User> example = Example.of(user, ExampleMatcher.matching().withIgnorePaths("uid", "create_date", "nick_name", "role"));
        return userMapper.findOne(example).orElse(null);
    }

    @Override
    public List<UserDTO> getUsers() {
        return UserDTO.userDTOS(userMapper.findAll());
    }

    /**
     * 获取用户的权限
     *
     * @param
     * @return 用户的权限列表
     */
    @Override
    public Set<Permission> getPermission(Role role) {
        return role.getPermission();
    }

    @Override
    public List<Permission> getAllPermission() {
        return permissionMapper.findAll();
    }

    /**
     * 获取用户的角色组
     *
     * @param token Shiro的token
     * @return 用户的角色组
     */
    @Override
    public Set<Role> getRole(Object token) {
        return (getUser(token).getRole());
    }

    @Override
    public Boolean changePermission(Long id, List<Integer> pids) {
        Role role = role(id);
        List<Long> longs = new ArrayList<>();
        for (Integer pid : pids) {
            longs.add(Long.valueOf(pid));
        }
        List<Permission> permissions = permissionMapper.findAllById(longs);
        role.setPermission(new HashSet<>(permissions));
        return addRole(role);
    }

    @Override
    public Boolean changeUserPermission(Long id, List<Integer> pids) {
        try {
            User user = getUser(id);
            List<Role> roles = roles(pids);
            user.setRole(new HashSet<>(roles));
            userMapper.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Role role(Long id) {
        return roleMapper.findById(id).get();
    }

    @Override
    public Boolean addRole(Role role) {
        return roleMapper.save(role).getRid() > 0;
    }

    @Override
    public Long savePermission(Permission permission) {
        return permissionMapper.save(permission).getPid();
    }

    @Override
    public Long saveUser(User user) {
        return userMapper.save(user).getUid();
    }

    @Override
    public List<Role> roles() {
        return roleMapper.findAll();
    }

    @Override
    public List<Role> roles(List<Integer> rids) {
        List<Long> longs = new ArrayList<>();
        for (Integer pid : rids) {
            longs.add(Long.valueOf(pid));
        }
        return roleMapper.findAllById(longs);
    }

    @Override
    public Role role(String name) {
        return roleMapper.findByName(name);
    }

    @Override
    public Boolean AlterRole(Role role) {
        return roleMapper.save(role).getRid() != 0;
    }

    @Override
    public List<UserDTO> search(UserVo userVo) {
        List<UserDTO> userDTOS = new ArrayList<>();
        userMapper.findAllByNickNameContaining(userVo.getNickName()).forEach((value) -> userDTOS.add(new UserDTO(value)));
        return userDTOS;
    }

    @Override
    public User createAdminUser(User user) {
        Role role = role("admin");
        if (role == null) {
            role = new Role();
            role.setPermission(new HashSet<>(this.getAllPermission()));
            role.setName("admin");
            role.setStatus(true);
            if (!this.addRole(role)) return null;
        }

        user.setRole(Collections.singleton(role));
        user.setCreateDate(System.currentTimeMillis());
//        user.setStatus(true);
        user.Md5Hash();
        return userMapper.save(user);
    }

    @Override
    public User getUserByRole(Role role) {
        List<User> users = userMapper.getAllByRoleOrderByCreateDateDesc(role);
        if (users == null || users.size() == 0) return null;
        return users.get(0);
    }
}
