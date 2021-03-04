package cn.j.netstorage.Entity.DTO;

import cn.j.netstorage.Entity.File.DeleteFile;
import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@ToString
//回收站和正确处理被删除的文件 设置定时器删除七天前的文件
public class DeleteDTOs {

    private List<DeleteDTO> list = new ArrayList<>();

    public DeleteDTOs() {
    }

    public DeleteDTOs(List<DeleteFile> files) {
        for (DeleteFile file : files) {
            list.add(new DeleteDTO(file));
        }
    }

    @Setter
    @Getter
    @ToString
    public static class DeleteDTO {
        private Long id;
        private String originDiskParentName;
        private String originDiskFileName;
        private Long size;

        private String type;
        private Long date;

        public DeleteDTO(DeleteFile file) {
            this.date = file.getDate();
            this.originDiskFileName = file.getOriginDiskFileName();
            this.originDiskParentName = file.getOriginDiskParentName();
            this.id = file.getId();
            this.size = file.getSize();
            this.type = file.getType();
        }
    }
}
