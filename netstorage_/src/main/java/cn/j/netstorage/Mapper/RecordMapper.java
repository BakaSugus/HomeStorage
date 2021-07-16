package cn.j.netstorage.Mapper;

import cn.j.netstorage.Entity.Record;
import cn.j.netstorage.Entity.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordMapper extends JpaRepository<VisitRecord,Long>{

    List<VisitRecord> getAllByParentFolder(String folder);

    List<VisitRecord> getAllByParentFolderAndSelfName(String folderName,String selfName);

    List<VisitRecord> getAllByParentFolderAndSelfNameAndOperationType(String folderName,String selfName,String operation_type);
}
