package cn.j.netstorage.Entity;

import com.google.gson.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@ToString
public class EMail {
    private String EMail;

    private String password;

    private List<String> accept;

    public EMail(JsonObject content) {
        JsonObject object = content.has("EMail")?content.getAsJsonObject("EMail"):null;

        if (object==null)return;

        String account = object.get("EMail").getAsString();
        String pwd = object.get("password").getAsString();
        if (StringUtils.isEmpty(account))return;

        if (StringUtils.isEmpty(pwd))return;

        JsonArray jsonArray = object.getAsJsonArray("list");

        if (accept == null) {
            accept = new ArrayList<>();
        }
        for (JsonElement element : jsonArray) {
            accept.add(element.getAsString());
        }
    }
}
