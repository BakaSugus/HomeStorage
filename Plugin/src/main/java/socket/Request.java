package socket;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.util.HashMap;

public class Request {

    private HashMap<String, Object> params = new HashMap<>();

    public Request(String content) {
        this.params = new Gson().fromJson(content, HashMap.class);

    }

    public String getParam(String key) {
        if (params != null && params.containsKey(key))
            return params.get(key).toString();
        return null;
    }

    public String[] getParams(String... keys) {
        String[] arr = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            arr[i] = getParam(keys[i]);
        }
        return arr;
    }

    public String getTYPE() {
        return getParam("type");
    }
//
//    public Request parse() {
//        String type = getParams("type");
//        tool.Resolve resolves = tool.Resolve.getResolveParams(type);
//        if (resolves == null)
//            return null;
//
//        this.Keys = resolves.getParams();
//        for (int i = 0; i < Keys.length; i++) {
//
//        }
//    }

}
