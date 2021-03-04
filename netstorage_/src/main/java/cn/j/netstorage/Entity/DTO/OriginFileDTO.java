package cn.j.netstorage.Entity.DTO;

import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.File.OriginFile;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
public class OriginFileDTO {

    private String path;
    private long id;


    public OriginFileDTO() {

    }

    public OriginFileDTO(OriginFile originFile) {
        HardDiskDevice hardDiskDevice = originFile.getHardDiskDevice() != null ? new ArrayList<HardDiskDevice>(originFile.getHardDiskDevice()).get(0) : new HardDiskDevice();
        this.path = hardDiskDevice.getCustomName()+"/"+originFile.getFileName();
        this.id = originFile.getOid();
    }

    public FilesDTO convertFilesDTO() {
        return convertFilesDTO(this);
    }

    public FilesDTO convertFilesDTO(OriginFileDTO originFileDTO) {
        return convertFilesDTO(new FilesDTO(), originFileDTO);
    }

    public FilesDTO convertFilesDTO(FilesDTO filesDTO, OriginFileDTO originFileDTO) {
        filesDTO.setSelfName(originFileDTO.getPath());
        return filesDTO;
    }

}
