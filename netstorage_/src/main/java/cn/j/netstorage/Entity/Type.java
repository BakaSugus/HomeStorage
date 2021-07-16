package cn.j.netstorage.Entity;

import java.util.*;

public enum Type {
    Music("Music"),
    Video("Video"),
    Other("Other"),
    Document("Document"),
    Folder("Folder"),
    Picture("Picture"),
    PDF("PDF"),
    Common("Common"),
    Torrent("Torrent"),
    Software("Software"),
    AndroidSoftware("AndroidSoftware"),
    AutoImport("AutoImport"),
    RAR("RAR"),
    Setting("Set"),
    Temp("Temp");
    String type;

    public String getType() {
        return this.type;
    }

    Type(String type) {
        this.type = type;
    }

    static String[] music = {".mp3", ".ape", ".flac", ".ogg", ".acc"};
    static String[] video = {".mp4", ".mkv", ".avi", ".rmvb", ".flv", ".f4v", ".m4s"};
    static String[] doc = {".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx"};
    static String[] picture = {".gif", ".jpg", ".jpeg", ".png", ".tif", ".tiff", ".bmp", ".svg"};
    static String[] pdf = {".pdf"};
    static String[] common = {".txt", ".log",".java", ".xml", ".md", ".py", ".php", ".html", ".js", ".vue", ".sql", ".config", ".properties", ".ini"};
    static String[] torrent = {".torrent"};
    static String[] PCSoftware = {".exe", ".msi"};
    static String[] androidSoftware = {".apk"};
    static String[] rar = {".zip",".rar"};

    public static Type getInstance(String ext) {
        int index=ext.lastIndexOf(".");
        if (index==-1){
            return Common;
        }
        ext = ext.substring(index);
        ext = ext.toLowerCase();


        if (Arrays.asList(music).contains(ext))
            return Music;

        if (Arrays.asList(video).contains(ext))
            return Video;

        if (Arrays.asList(doc).contains(ext))
            return Document;

        if (Arrays.asList(picture).contains(ext))
            return Picture;

        if (Arrays.asList(pdf).contains(ext))
            return PDF;

        if (Arrays.asList(common).contains(ext))
            return Common;

        if (Arrays.asList(torrent).contains(ext))
            return Torrent;

        if (Arrays.asList(PCSoftware).contains(ext))
            return Software;

        if (Arrays.asList(androidSoftware).contains(ext))
            return AndroidSoftware;

        if (Arrays.asList(rar).contains(ext))
            return RAR;
        return Other;
    }

    public static Boolean equals(String ext, String... target) {
        ext = ext.toLowerCase();
        for (String aTarget : target) {
            if (ext.equals("." + aTarget)) return true;
        }
        return false;
    }
}
