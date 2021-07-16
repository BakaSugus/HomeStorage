package cn.j.netstorage.Entity.Log;

import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.text.SimpleDateFormat;
import java.util.Date;

@Setter
@Getter
public class Log {


    private long timestamp;

    private String message;

    private String type;

    private String result;

    private User user;

    private String ex;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Log(long timestamp, String message, String type, boolean result, User user) {
        this.timestamp = timestamp;
        this.message = message;
        this.type = type;
        this.result = result?"成功":"失败";
        this.user = user;
    }

    public Log(long timestamp, String message, String type, boolean result, User user, String ex) {
        this.timestamp = timestamp;
        this.message = message;
        this.type = type;
        this.result = result?"成功":"失败";
        this.user = user;
        this.ex = ex;
    }

    public String toString(){
        if (ex==null)
            return String.format("%s -- %s用户 执行了 %s 执行结果为:%s",sdf.format(new Date(this.timestamp)),this.user.getEmailAccount(),this.message,this.result);
        return String.format("%s --  %s用户 执行了 %s 执行结果为: %s 存在异常:%s",sdf.format(new Date(this.timestamp)),this.user.getEmailAccount(),this.message,this.result,this.ex);
    }
}
