package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.Usage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageMapper extends JpaRepository<Usage,Long>{
    //Usage findByName(String type);
}
