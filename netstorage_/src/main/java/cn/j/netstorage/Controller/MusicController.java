package cn.j.netstorage.Controller;

import cn.j.netstorage.Entity.DTO.MusicsDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.plugin.MusicCollection;
import cn.j.netstorage.Service.MusicCollectionService;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/Music/")
public class MusicController {

    @Autowired
    private MusicCollectionService musicCollectionService;

    @Autowired
    private UserService userService;

    @GetMapping("/Music")
    public ResultBuilder<List<Files>> getMusics() {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj == null) {
            return new ResultBuilder(StatusCode.FALL);
        }
        User user = userService.getUser(obj.toString());
        List<MusicsDTO.MusicDTO> filesList = musicCollectionService.getMusic(user);
        return new ResultBuilder(filesList, StatusCode.SUCCESS);
    }

    /**
     * 获得这个用户的所有歌单
     *
     * @return
     */
    @GetMapping("/MusicCollection")
    public ResultBuilder Music() {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj == null) {
            return new ResultBuilder(StatusCode.FALL);
        }

        List<MusicCollection> collections=musicCollectionService.Musics(userService.getUser(obj.toString()));
        List<MusicsDTO> list=new ArrayList<>();
        if (collections!=null){
            collections.forEach(value->{
                list.add(new MusicsDTO(value));
            });
        }
        return new ResultBuilder<>(list, StatusCode.SUCCESS);
    }


    /**
     * 获得这个歌单的所有音乐
     *
     * @param id
     * @return
     */
    @GetMapping("/MusicCollection/{id}")
    public ResultBuilder Music(@PathVariable("id") Long id) {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj == null)
            return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder<MusicCollection>(musicCollectionService.Music(id), StatusCode.SUCCESS);
    }

    /**
     * 删除一个歌单
     *
     * @param id
     * @return
     */
    @DeleteMapping("/MusicCollection/{id}")
    public ResultBuilder delMusicCollection(@PathVariable("id") Long id) {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj == null) {
            return new ResultBuilder(StatusCode.FALL);
        }
        return new ResultBuilder<Boolean>(musicCollectionService.delete(id, userService.getUser(obj.toString())), StatusCode.SUCCESS);
    }

    /**
     *
     * @param id 歌单id
     * @param fid 歌曲id
     * @return
     */
    @DeleteMapping("/Music/{id}/{fid}")
    public ResultBuilder delMusic(@PathVariable("id") Long id, @PathVariable("fid") Long fid) {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj == null) {
            return new ResultBuilder(StatusCode.FALL);
        }
        return new ResultBuilder<>(
                musicCollectionService.deleteMusic(
                        id, fid, userService.getUser(obj.toString())),
                StatusCode.SUCCESS);
    }

    /**
     * 新增一个歌单
     *
     * @return
     */
    @PutMapping("/MusicCollection")
    public ResultBuilder MusicCollection(MusicCollection musicCollection) {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj == null) {
            return new ResultBuilder(StatusCode.FALL);
        }


        musicCollection.setUser(userService.getUser(obj.toString()));
        return new ResultBuilder<Boolean>(
                musicCollectionService.add(musicCollection),
                StatusCode.SUCCESS);
    }


    /**
     * 对歌单新增一个歌曲
     *
     * @param id
     * @return
     */
    @PutMapping("/Music/{id}/{music}")
    public ResultBuilder MusicCollection(@PathVariable("id") Long id, @PathVariable("music") Long music) {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj == null) {
            return new ResultBuilder(StatusCode.FALL);
        }
        return new ResultBuilder<Boolean>(
                musicCollectionService.addMusic(id, music, userService.getUser(obj.toString()))
                , StatusCode.SUCCESS);
    }

    @GetMapping("/music")
    public ResultBuilder MusicList(Long id){
        Object obj=SecurityUtils.getSubject().getPrincipal();
        if (obj==null){
            return new ResultBuilder(StatusCode.FALL);
        }
        return new ResultBuilder(musicCollectionService.getMusic(id,userService.getUser(obj.toString())),StatusCode.SUCCESS);
    }

    @GetMapping("musicLinks")
    public ResultBuilder MusicLists(Long cid){
        return new ResultBuilder(StatusCode.FALL);
    }
}
