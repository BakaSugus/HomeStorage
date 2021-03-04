package cn.j.netstorage.tool;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class HashCodeUtil {

    public static String getHashCode(String path){
        try {
            return getHashCode(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getHashCode(File file)throws IOException{
        return getHashCode(new FileInputStream(file));
    }

    public static String getHashCode(MultipartFile file)throws IOException{
        return getHashCode(file.getInputStream());
    }

    public static String getHashCode(InputStream fileInputStream){
        try {
            //拿到一个MD5转换器,如果想使用SHA-1或SHA-256，则传入SHA-1,SHA-256
            MessageDigest md = MessageDigest.getInstance("MD5");

            //分多次将一个文件读入，对于大型文件而言，比较推荐这种方式，占用内存比较少。
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fileInputStream.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, length);
            }
            fileInputStream.close();
            //转换并返回包含16个元素字节数组,返回数值范围为-128到127
            byte[] md5Bytes  = md.digest();
            BigInteger bigInt = new BigInteger(1, md5Bytes);//1代表绝对值
            return bigInt.toString(16);//转换为16进制
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
}
