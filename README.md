# 介绍
这个项目主要是实现的是文件的存储，整合起一部分零零散散的数据。通过留下几个http的接口，让插件调用这些接口来扩展出各种各样的用途，说白了就是把云搬回家里。比如说转码，pdf与doc/excel/ppt的互相转化，dlna,bt下载，rss订阅，m3u8下载，云打印（垃圾菜鸟打印组件），定时采集任务，甚至接几个zigbee的设备或许就能介入智能家居（虽然我不会）

***

# 功能
- [x] 上传，下载文件
- [x] 多用户
- [x] 磁盘映射管理
- [x] 版本控制
- [x] 投屏 需搭配插件使用 
- [x] 基本文件预览
- [x] bt下载 (需自行下载aria2)
- [x] 减少重复文件上传
- [x] 压缩下载 批量下载
- [x] 权限控制
- [x] 角色管理
- [x] 前后端分离
- [x] 分片上传
- [x] 快速上传已存在的文件

- [x] 文件夹共享
- [x] 重命名
- [x] 批量下载

- [ ] URL资源嗅探下载
- [ ] 共享资源的编辑和版本控制的协调
***

# 使用
``` Shell
java -jar netstorage-0.0.1-SNAPSHOT.jar --ip=192.168.1.121 --server.tomcat.basedir=/mnts1/HomeStorage/ --tempDir=/mnts1/HomeStorage/temp/
```
首先运行jar包 以上参数参考自己的硬盘和局域网情况和数据库情况  然后运行sql文件 然后把静态资源扔到nginx 设置反向代理,设置如下，浏览器输入http://{IP}
```
server {
    listen       80;
    # 服务器名称
    server_name  localhost;
    proxy_connect_timeout 600;

    proxy_read_timeout 600;

    proxy_send_timeout 600;
    # 路径配置
    location / {
        # 相对路径配置，基于nginx启动的位置
        root   /usr/local/dist;
        index  index.html;
        
        try_files $uri $uri/ @router;  
    }
    
    location @router {
        rewrite (static/.*)$ /$1   redirect;
        rewrite ^.*$   /index.html  last;
     }
  
     location /api/ {
        #  反向代理
        proxy_pass http://127.0.0.1:8020/;
     }
}
```
