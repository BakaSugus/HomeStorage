package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordMapper extends JpaRepository<VisitRecord,Long>{
}
