package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.File.OriginFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OriginFileMapper extends JpaRepository<OriginFile,Long> {

    OriginFile getOriginFileByFileName(String FileName);

    OriginFile findByMd5(String md5);

    int countAllByFileName(String name);


}
