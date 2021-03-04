package cn.j.netstorage.Entity.DTO;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.plugin.MusicCollection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@ToString
public class MusicsDTO extends BaseDTO {

    private Set<MusicDTO> filesDTOS;

    public MusicsDTO(){}

    public MusicsDTO(MusicCollection collection){
        this(collection.getCollectionName(),collection.getId(),collection.getSongFiles(),collection.getDate());
    }

    private MusicsDTO(String name, Long id, Set<Files> files, Date date){
        this.name=name;
        this.id=id;
        this.createDate=date;

        if (filesDTOS==null)
            filesDTOS=new HashSet<>();

        files.forEach((value)->{
            filesDTOS.add(new MusicDTO(value));
        });
    }

    @ToString
    @Setter
    @Getter
    public static class MusicDTO {
        private String title;
        private Long id;
        private String src;

        public MusicDTO(){

        }
        public MusicDTO(Files files){

            Set<OriginFile> originFile=files.getOriginFile();
            if (originFile==null||originFile.size()<1){
                return;
            }
            this.title=files.getSelfName();
            this.id=files.getFid();
            this.src="/api/"+originFile.iterator().next().getCustomPath();
        }


    }

}
