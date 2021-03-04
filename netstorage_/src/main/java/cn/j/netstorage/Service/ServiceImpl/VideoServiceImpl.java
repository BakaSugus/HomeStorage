package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.DTO.VideoDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.plugin.VideoCollection;
import cn.j.netstorage.Mapper.VideoMapper;
import cn.j.netstorage.Service.FilesService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.Service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private UserService userService;

    @Autowired
    private FilesService filesService;

    @Autowired
    private VideoMapper videoMapper;

    @Override
    public VideoCollection video(Long id, User user) {
        if (user == null) return null;
        VideoCollection collection = videoMapper.findById(id).get();
        if (!user.getEmailAccount().equals(collection.getUser())) return null;
        return collection;
    }

    @Override
    public List<VideoCollection> videos(User user) {
        if (user == null) return null;
        List<VideoCollection> videos = videoMapper.findAllByUserIs(user);
        return videos;
    }

    @Override
    public Boolean DelVideoCollect(Long id, User user) {
        return null;
    }

    @Override
    public Boolean DelVideo(Long vcid, Long vid, User user) {
        return null;
    }

    @Override
    public Boolean addVideos(VideoCollection videoCollection, User user) {
        videoCollection.setUser(user);
        return videoMapper.save(videoCollection).getId() != 0;

    }

    @Override
    public Boolean addVideo(Long collection, Long video, User user) {
        if (collection == null||collection==0) return false;
        if (video == 0) return false;
        if (user == null) return false;
        VideoCollection collections=video(collection,user);

        Files files = filesService.findByFid(video);
        if (!Type.Video.getType().equals(files.getType())) return false;

        Set<Files> filesSet = collections.getFiles();
        if (filesSet == null) return false;

        filesSet.add(files);
        collections.setFiles(filesSet);

        try {
            videoMapper.save(collections);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public VideoDTO.Video getVideo(Long id, User user) {
        if (id == 0) return null;
        if (user == null) return null;
        Files files = filesService.findByFid(id);
        if (!Type.Video.getType().equals(files.getType())
                || files.getUser() == null ||
                files.getUser().size() < 1 ||
                files.getUser().get(0).getEmailAccount().equals(user.getEmailAccount())) return null;

        return new VideoDTO.Video(files);

    }

    @Override
    public List<VideoDTO> videoCollectToDTO(List<VideoCollection> collect) {
        if (collect == null || collect.size() < 1) return new ArrayList<>();
        List<VideoDTO> videoCollections = new ArrayList<>();
        collect.forEach((value) -> {
            videoCollections.add(new VideoDTO(value));
        });
        return videoCollections;
    }

    @Override
    public List<VideoDTO.Video> getAllVideos(User user) {
        List<VideoDTO.Video> list=new ArrayList<>();
        List<Files> files=filesService.getByType(user,Type.Video);
        if (files==null||files.size()<1)return null;

        files.forEach(value->{
            list.add(new VideoDTO.Video(value));
        });

        return list;
    }
}
