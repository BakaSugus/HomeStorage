package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.User.Token;
import cn.j.netstorage.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenMapper extends JpaRepository<Token, Long> {
    Token findByToken(String token);

    Token findByUser(User user);
}
