package cn.j.netstorage.Entity.Log;

import cn.j.netstorage.Entity.User.User;

public class LogTemplate {

    public static Log create(String message, String type, User user,boolean result,String ex){
        if (ex==null)
            return new Log(System.currentTimeMillis(),message,type,result,user);
        return new Log(System.currentTimeMillis(),message,type,result,user,ex);
    }

    public static Log UploadLog(User user,String path,boolean result,String ex){
        return create("上传文件:"+path,"Upload",user,result,ex);
    }

    public static Log DeleteLog(User user,String path,boolean result,String ex){
        return create("删除了:"+path,"Delete",user,result,ex);
    }

    public static Log UpdateLog(User user,String path,boolean result,String ex){
        return create("修改了:"+path,"Update",user,result,ex);
    }

    public static Log initLog(User user,String item, String message,boolean result,String ex){
        return create("初始化"+item +":"+message,"Init",user,result,ex);
    }

    public static Log ShareLog(User user,String path,boolean result,String ex){
        return create("共享文件夹:"+path,"Share",user,result,ex);
    }

    public static Log BackUp(User user,String path,boolean result,String ex){
        return create("备份了:"+path,"BackUp",user,result,ex);
    }

    public static Log Open(User user,String path,boolean result,String ex){
        return create("打开了:"+path,"Open",user,result,ex);
    }
}
