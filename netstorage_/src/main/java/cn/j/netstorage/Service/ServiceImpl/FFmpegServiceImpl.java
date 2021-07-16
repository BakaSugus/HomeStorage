package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.HardDiskDevice;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.FFmpegService;
import cn.j.netstorage.Service.FileService2;
import cn.j.netstorage.Service.OriginFileService;
import cn.j.netstorage.Service.UploadService;
import cn.j.netstorage.tool.FFMPEG;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


@Service
public class FFmpegServiceImpl implements FFmpegService {

    @Autowired
    private UploadService uploadService;

    @Autowired
    private FileService2 fileService2;

    @Autowired
    private OriginFileService originFileService;

    private final String Convert = "Convert";

    private final String SplitMusic = "SplitMusic";

    private final String SplitVideo = "SplitVideo";

    private boolean run(String commond, String type, Files files) {
        Integer code = null;
        FFMPEG ffmpegCmd = new FFMPEG();
        /**
         * 错误流
         */
        InputStream errorStream = null;
        try {
            ffmpegCmd.execute(false, true, commond);
            errorStream = ffmpegCmd.getErrorStream();

            //打印过程
            int len = 0;
            while ((len = errorStream.read()) != -1) {
                System.out.print((char) len);
            }

            //code=0表示正常
            code = ffmpegCmd.getProcessExitCode();
            if (code == 0) {
                switch (type) {
                    case SplitMusic:
                        break;
                    case Convert:
                        break;
                    case SplitVideo:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            ffmpegCmd.close();
        }
        //返回
        return code != null && code == 0;
    }

    @Override
    public boolean convertVideo(Files files) {
        String fullName = files.getFullName();
        String name = files.getName();
        OriginFile originFile = files.getOriginFile();
        File file = new File(originFile.getPath());
        file = file.getParentFile();

        File target = new File(file.getAbsolutePath(), name + ".mp4");
        String command = FFMPEG.Convert(fullName, "mp4", target.getAbsolutePath());
        try {
            run(command, SplitMusic, files);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean OnCallConvertVideo(Files files) {
        OriginFile originFile = files.getOriginFile();
        String path = originFile.getPath();
        if (StringUtils.isEmpty(path))
            return false;
        String originName = files.getSelfName();
        int count = originName.lastIndexOf(".");
        String targetName = originName.substring(0,count);
        targetName += "mp4";
        files.setSelfName(targetName);
        return false;
    }

    @Override
    public boolean splitMusic(Files files) {
        String fullName = files.getFullName();
        String name = files.getName();
        OriginFile originFile = files.getOriginFile();
        File file = new File(originFile.getPath());
        file = file.getParentFile();

        File target = new File(file.getAbsolutePath(), name + ".mp3");
        String commond = FFMPEG.SplitMusic(fullName, target.getAbsolutePath());
        StringBuilder sb = new StringBuilder();
        sb.append(commond);
        try {
            run(sb.toString(), SplitMusic, files);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean OnCallSplitMusic(Files files) {
        String fullName = files.getOriginFile().getPath();

        HardDiskDevice dev = files.getOriginFile().getHardDiskDevice().iterator().next();
        String targetNameInDisk = dev.get().getAbsolutePath() + System.currentTimeMillis() + "." + files.getExt();

        return changeNameAndSave(files, targetNameInDisk, dev);
    }

    public boolean changeNameAndSave(Files files, String targetNameInDisk, HardDiskDevice dev) {

        Type type = Type.getInstance(files.getSelfName());
        try {
            OriginFile originFile = originFileService.originFile(new File(targetNameInDisk), dev);

            if (originFile == null) return false;

            originFile = originFileService.saveOriginFile(originFile);
            if (originFile.getOid() == 0) return false;
            Files finalFIles = uploadService.getFinalName(files.getOneUser(), files.getOneUser(), files.getParentName(), files.getSelfName(), type, originFile);
            return fileService2.save(finalFIles);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
