package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.DTO.MusicsDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.plugin.MusicCollection;
import cn.j.netstorage.Mapper.MusicMapper;
import cn.j.netstorage.Service.FilesService;
import cn.j.netstorage.Service.MusicCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class MusicCollectionServiceImpl implements MusicCollectionService{


    @Autowired
    private MusicMapper musicMapper;

    @Autowired
    private FilesService filesService;

    @Override
    public List<MusicCollection> Musics(User user) {
        return musicMapper.findAllByUserIs(user);
    }

    @Override
    public MusicCollection Music(Long id) {
        MusicCollection musicCollection=musicMapper.findById(id).get();
        return musicCollection;
    }

    @Override
    public Boolean delete(Long id,User user) {
        MusicCollection collection=musicMapper.findById(id).get();
        if (user== null||collection==null||!user.getEmailAccount().equals(collection.getUser().getEmailAccount())){
            return false;
        }

        try {
            musicMapper.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean deleteMusic(Long targetMusicCollection, Long targetId, User targetCreator) {

        MusicCollection musicCollection=musicMapper.findById(targetMusicCollection).get();

        if (musicCollection==null||!targetCreator.getEmailAccount().equals(musicCollection.getUser().getEmailAccount())){
            return false;
        }

        Set<Files> songs=musicCollection.getSongFiles();

        Files files=filesService.findByFid(targetId);

        songs.remove(files);

        musicCollection.setSongFiles(songs);

        return musicMapper.save(musicCollection).getId()!=0;
    }

    @Override
    public Boolean add(MusicCollection musicCollection) {
        return musicMapper.save(musicCollection).getId()!=0;
    }

    @Override
    public Boolean addMusic(Long id,Long files,User Creator) {

        MusicCollection musicCollection=Music(id);

        if (Creator==null)
            return false;

        if (musicCollection==null||!musicCollection.getUser().getEmailAccount().equals(Creator.getEmailAccount()))
            return false;

        Files target = filesService.findByFid(files);
        if (target==null||(target.getUser().size()==1&&!Creator.getEmailAccount().equals(target.getUser().get(0).getEmailAccount())))
            return false;

        if (!Type.Music.getType().equals(target.getType()))
            return false;

        Set<Files> songs=musicCollection.getSongFiles();
        Boolean res = songs.add(target);
        musicCollection.setSongFiles(songs);

        return res && musicMapper.save(musicCollection).getId() != 0;
    }


    @Override
    public Boolean importMusic(String url) {
        //解析并下载并导入
        return null;
    }

    @Override
    public Boolean exportMusic(MusicCollection musicCollection) {
        //写一个根据id直接添加音乐的压缩方法 将原本的抽象出来做成工具类 用匿名类传入方法执型
        return null;
    }

    @Override
    public List<MusicsDTO.MusicDTO> getMusic(User user) {
        List<Files> files=filesService.getByType(user,Type.Music);
        List<MusicsDTO.MusicDTO> list=new ArrayList<>();
        files.forEach(value->{
            list.add(new MusicsDTO.MusicDTO(value));
        });
        return list;
    }

    @Override
    public MusicsDTO.MusicDTO getMusic(Long id, User user) {
        Files files=filesService.findByFid(id);
        if (files==null)
            return null;

        if (!Type.Music.getType().equals(files.getType()))
            return null;

        if (!user.getEmailAccount().equals(files.getUser().get(0).getEmailAccount()))
            return null;

        return new MusicsDTO.MusicDTO(files);
    }

}
