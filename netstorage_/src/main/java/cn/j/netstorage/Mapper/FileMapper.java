package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface FileMapper extends JpaRepository<Files, Long> {

    List<Files> findAllByParentNameAndUserAndType(String parentName, User user, String type);

    List<Files> findAllByParentNameAndUserAndVisible(String parentName, User user,  boolean Visible);

    Files findByParentNameAndUserAndSelfName(String parentName, User user, String selfName);

    Integer deleteAllByFidIsOrParentNameIsLikeAndUser(Long fid, String parentName, User user);

    Integer deleteByFid(Long fid);

    List<Files> findAllBySelfNameContainingAndUserIs(String selfName, User user);

    List<Files> findAllBySelfNameAndUserAndParentName(String selfName, User user, String parentName);

    List<Files> findAllBySelfNameLikeAndUserAndParentName(String selfName, User user, String parentName);

    List<Files> findByUserAndType(User user, String type);

    List<Files> findByParentNameLikeAndUser(String parentName, User user);

    int countAllByOriginFile(OriginFile originFile);

    Files findAllByUserAndOriginFile(User user, OriginFile originFile);

}
