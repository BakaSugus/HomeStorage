package cn.j.netstorage.tool;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.StringUtils;

import java.util.*;

public class FilesUtil {

    public static String append(String parent, String son) {
        if (!parent.endsWith("/"))
            parent += "/";
        if (StringUtils.hasText(son))
            parent += son;
        return parent;
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

    public static User setUser(Long uid) {
        User user = new User();
        user.setUid(uid);
        return user;
    }

    public static Set<User> setUserSet(Long uid) {
        return Collections.singleton(setUser(uid));
    }

    public static List<User> setUserList(Long uid) {
        return Collections.
                singletonList(setUser(uid));
    }

    public static String getCurrentNameWithExt(String ext) {
        return System.currentTimeMillis() + ext;
    }

    public static Boolean delete(JpaRepository jpaRepository, Object object) {
        try {
            jpaRepository.delete(object);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static <T> T convert(Set<T> set) {
        return set != null ? new ArrayList<T>(set).get(0) : null;
    }

    public static <T> Set<T> convert(T t) {
        return Collections.singleton(t);
    }

    public static Files createFolder(String folderName, String parentName, List<User> users) {
        Files files = new Files();
        files.setSelfName(folderName);
        files.setParentName(parentName);
        files.setCreateDate(new Date());
        files.setUser(users);
        return files;
    }

    public static Files createFiles(String selfName, String parentName, List<User> users) {
        Files files = new Files();
        files.setUser(users);
        files.setCreateDate(new Date());
        files.setSelfName(selfName);
        files.setParentName(parentName);
        return files;
    }
}
