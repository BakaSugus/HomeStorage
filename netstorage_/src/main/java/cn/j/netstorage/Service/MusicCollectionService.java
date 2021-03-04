package cn.j.netstorage.Service;

import cn.j.netstorage.Entity.DTO.MusicsDTO;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.plugin.MusicCollection;

import java.util.List;

public interface MusicCollectionService {
    List<MusicCollection> Musics(User user);

    MusicCollection Music(Long id);

    Boolean delete(Long id,User createUser);

    Boolean deleteMusic(Long targetMusicCollection,Long targetId,User targetCreator);

    Boolean add(MusicCollection musicCollection);

    Boolean addMusic(Long id,Long files,User Creator);

    Boolean importMusic(String url);

    Boolean exportMusic(MusicCollection musicCollection);

    List<MusicsDTO.MusicDTO> getMusic(User user);

    MusicsDTO.MusicDTO getMusic(Long id, User user);
}
