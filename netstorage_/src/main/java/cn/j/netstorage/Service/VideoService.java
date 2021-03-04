package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.DTO.VideoDTO;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.plugin.VideoCollection;

import java.util.List;

public interface VideoService {


    VideoCollection video(Long id,User user);

    List<VideoCollection> videos(User user);

    Boolean DelVideoCollect(Long id,User user);

    Boolean DelVideo(Long vcid,Long vid,User user);

    Boolean addVideos(VideoCollection videoCollection,User user);

    Boolean addVideo(Long collection, Long video, User user);

    VideoDTO.Video getVideo(Long id,User user);

    List<VideoDTO> videoCollectToDTO(List<VideoCollection> collect);

    List<VideoDTO.Video> getAllVideos(User user);

}
