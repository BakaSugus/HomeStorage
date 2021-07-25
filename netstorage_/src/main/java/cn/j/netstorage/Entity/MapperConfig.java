package cn.j.netstorage.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;

@Setter
@Getter
@ToString
public class MapperConfig {
    private String auto_encoding;//视频转码
    private String size = "10";//空间大小限制

    public HashMap<String,String> getDefaultTemplate(){
        HashMap<String,String> map =new HashMap<>();
        map.put("auto_encoding",auto_encoding);
        map.put("size",size);
        return map;
    }

    public static HashMap<String,String> getCustomTemplate( String auto_encoding,String size){
        HashMap<String,String> map =new HashMap<>();
        map.put("auto_encoding",auto_encoding);
        map.put("size",size);
        return map;
    }

    public MapperConfig toMapperConfig(boolean auto_encoding, int size){
        this.auto_encoding=Boolean.toString(auto_encoding);
        this.size=Integer.toString(size);
        return this;
    }


}
