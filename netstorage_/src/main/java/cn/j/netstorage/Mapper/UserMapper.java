package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.User.Role;
import cn.j.netstorage.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Resource;
import java.util.List;


public interface UserMapper extends JpaRepository<User,Long> {
    List<User> findAllByNickNameContaining(String nickName);

    List<User> getAllByRoleOrderByCreateDateDesc(Role role);
}
