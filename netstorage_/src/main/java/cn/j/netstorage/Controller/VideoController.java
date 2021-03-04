package cn.j.netstorage.Controller;

import cn.j.netstorage.Entity.DTO.VideoDTO;
import cn.j.netstorage.Entity.plugin.VideoCollection;
import cn.j.netstorage.Service.UserService;
import cn.j.netstorage.Service.VideoService;
import cn.j.netstorage.tool.ResultBuilder;
import cn.j.netstorage.tool.StatusCode;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Video")
public class VideoController {

    @Autowired
    VideoService videoService;

    @Autowired
    UserService userService;

    /**
     * 获得该视频列表的内容
     *
     * @param id
     * @return
     */
    @GetMapping("/Video/{id}")
    public ResultBuilder Video(@PathVariable("id") Long id) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);

        return new ResultBuilder<>(
                new VideoDTO(
                        videoService.video(id,
                                userService.getUser(object.toString()))),
                StatusCode.SUCCESS);
    }

    @GetMapping("/videos")
    public ResultBuilder getAllVideo(){
        Object object=SecurityUtils.getSubject().getPrincipal();
        if (object==null)
            return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder<>(videoService.getAllVideos(userService.getUser(object.toString())),StatusCode.SUCCESS);
    }


    /**
     * 获得该用户所有的视频列表
     *
     * @return
     */
    @GetMapping("/Videos")
    public ResultBuilder Videos() {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder<>(videoService.videos(userService.getUser(object.toString())), StatusCode.SUCCESS);
    }

    /**
     * 删除某个视频列表
     *
     * @param id
     * @return
     */
    @DeleteMapping("/Videos/{id}")
    public ResultBuilder delVideo(@PathVariable("id") Long id) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder<>(videoService.DelVideoCollect(id, userService.getUser(object.toString())), StatusCode.SUCCESS);
    }

    /**
     * 删除视频列表里面某个视频
     *
     * @param id
     * @param fid
     * @return
     */
    @DeleteMapping("/Video/{id}/{fid}")
    public ResultBuilder Videos(@PathVariable("id") Long id, @PathVariable("fid") Long fid) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder<>(videoService.DelVideo(id, fid, userService.getUser(object.toString())), StatusCode.SUCCESS);
    }

    /**
     * 获得某个id的视频
     *
     * @param id
     * @return
     */
    @GetMapping("/video/{id}")
    public ResultBuilder getVideo(@PathVariable("id") Long id) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);

        return new ResultBuilder<>(videoService.getVideo(id, userService.getUser(object.toString())), StatusCode.SUCCESS);
    }

    /**
     * 新增视频列表
     *
     * @return
     */
    @PutMapping("/Videos")
    public ResultBuilder addVideos(VideoCollection videoCollection) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder<>(videoService.addVideos(
                videoCollection,
                userService.getUser(object.toString())
        ), StatusCode.SUCCESS);
    }

    /**
     * 为某个视频列表新增一个视频
     *
     * @param id
     * @param targetVideoId
     * @return
     */
    @PutMapping("/Video/{id}")
    public ResultBuilder addVideo(@PathVariable("id") Long id, Long targetVideoId) {
        Object object = SecurityUtils.getSubject().getPrincipal();
        if (object == null)
            return new ResultBuilder(StatusCode.FALL);
        return new ResultBuilder<>(videoService.addVideo(id,targetVideoId,userService.getUser(object.toString())),StatusCode.SUCCESS);
    }

}
