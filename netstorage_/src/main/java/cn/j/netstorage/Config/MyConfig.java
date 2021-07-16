package cn.j.netstorage.Config;

import cn.j.netstorage.Entity.Config;
import cn.j.netstorage.Entity.EMail;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.oss.Oss;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Configuration
public class MyConfig {


    @Value("${configPath}")
    private String filePath;

    @Bean
    public Config getMyConfig() {
        System.out.println(filePath);
        JsonObject content = getFile();
        System.out.println("Content:" + content);
        if (content == null) return new Config();

        Config config = new Config();
        config.setAdmin(getAdminUser(content));
        config.setOss(getBackUpOss(content));
        config.setDevice_path(getDevice(content));

        config.setMaxSize(Size(content));
        config.setEMail(new EMail(content));
        config.setMaxSize(Size(content));
        return config;
    }

    private boolean has(JsonObject content, String key) {
        return content != null && content.has(key);
    }

    public JsonObject getFile() {

        if (!StringUtils.hasText(filePath)) return null;
        File file = new File(filePath);
        try {
            return new GsonBuilder().disableHtmlEscaping().create().fromJson(new FileReader(file), JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public User getAdminUser(JsonObject content) {

        if (!has(content, Config.ADMIN)) return null;
        JsonObject admin = content.getAsJsonObject(Config.ADMIN);

        String account = admin.get("user").getAsString();
        String password = admin.get("password").getAsString();
        String nickname = admin.get("nickname").getAsString();
        User user = new User();

        user.setEmailAccount(account);
        user.setNickName(nickname);
        user.setPassword(password);
        return user;
    }

    public Oss getBackUpOss(JsonObject content) {
        if (!has(content, Config.BACKUP_OSS)) return null;

        JsonObject oss = content.getAsJsonObject(Config.BACKUP_OSS);

        String region = oss.get("region").getAsString();
        String id = oss.get("id").getAsString();
        String key = oss.get("key").getAsString();

        Oss oss1 = new Oss();
        oss1.setRegion(region);
        oss1.setSecretId(id);
        oss1.setSecretKey(key);
        return oss1;
    }

    public HashMap<String,String> getDevice(JsonObject content) {
        if (!has(content, Config.DEVICE)) return null;
        JsonObject json = content.get(Config.DEVICE).getAsJsonObject();
        HashMap<String, String> map = new HashMap<>();
        Type[] list = Type.values();
        for (Type type : list) {
            if (json.has(type.getType())) {
                map.put(type.getType(), json.get(type.getType()).getAsString());
            } else {
                map.put(type.getType(), new File("").getAbsolutePath());
            }
        }
        return map;
    }


    public int Size(JsonObject content) {
        if (!has(content, Config.MAXSIZE)) return -1;

        return content.getAsJsonObject(Config.MAXSIZE).getAsInt();
    }
}
