package cn.j.netstorage.Entity.DTO;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.plugin.VideoCollection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@ToString
public class VideoDTO extends BaseDTO {

    private Set<Video> videos;

    public VideoDTO() {
    }

    public VideoDTO(VideoCollection videoCollection) {
        Set<Files> filesSet = videoCollection.getFiles();
        User user = videoCollection.getUser();
        if (filesSet == null || filesSet.size() < 1) return;
        if (user == null || user.getEmailAccount().equals("")) return;

        Set<Video> videos = this.videos == null ? new HashSet<>() : this.videos;
        filesSet.forEach(value -> {
            OriginFile originFile = value.getOriginFile();
            if (originFile != null) {
                videos.add(new Video(value.getFid(), value.getSelfName(), originFile.getCustomPath()));
            }
        });

        this.videos = videos;
        this.id = videoCollection.getId();
        this.name = videoCollection.getName();
        this.spec = videoCollection.getSpec();

    }

    public VideoDTO(Long id, String name, Set<Video> videos) {
        this.id = id;
        this.name = name;
        this.videos = videos;
    }


    @Setter
    @Getter
    @ToString
    public static class Video {
        private Long id;
        private String title;
        private String src;

        public Video(Long id, String title, String src) {
            this.id = id;
            this.title = title;
            this.src = src;
        }

        public Video(Files files) {
            Set<OriginFile> originFiles= Collections.singleton(files.getOriginFile());
            if(originFiles==null||originFiles.size()<1) return ;

            this.src=originFiles.iterator().next().getCustomPath();
            this.title = files.getSelfName();
            this.id = files.getFid();
        }
    }
}
