package tool;

import java.util.HashMap;

public class Ext {

    private static HashMap<String, String> extList = new HashMap<String, String>();

    static {
        put("Music", "mp3", "flac", "ogg");
        put("Video", "mp4", "rmvb", "flv", "avi");
        put("PDF", "pdf");
        put("Word", "doc", "docx");
        put("Picture", "jpg", "jpeg", "gif", "png");
    }

    private static void put(String value, String... key) {
        for (String k : key) {
            extList.put(k, value);
        }
    }

    public static String getType(String ext) {
        return extList.containsKey(ext.toLowerCase()) ? extList.get(ext.toLowerCase()) : "";
    }

    public static String GetTypeByFileName(String fileName){
        return getType(getExt(fileName));
    }

    public static String[] getFileNameAndExt(String fileName) {
        String[] strings = new String[3];
        strings[0] = "false";
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            strings[0] = "true";
            strings[1] = fileName.substring(0, index);
            strings[2] = fileName.substring(index);
        }
        return strings;
    }

    public static String getExt(String fileName) {
        String[] strings = getFileNameAndExt(fileName);
        return strings[0].equals("true") ? strings[2] : "";
    }


}
