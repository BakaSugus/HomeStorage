# 介绍
此项目是一个拥有基本功能的使用对象存储的局域网网盘, 目前支持使用腾讯云的对象存储和本地存储, 网盘的主体使用springboot和mysql完成(有点想换成h2),
最终目的是实现脚本文件下载的整合和挂机省电下载的，如脚本下载完图片就通过接口上传到网盘，强迫症发作要查看不在身边的文件的内容，百度云挂机下载(虽然不一定做得到)
***

# 功能
- [x] 上传，下载文件
- [x] 多用户
- [x] 基本权限 
- [x] 投屏 需搭配插件使用 
- [x] 基本文件预览
- [x] 压缩下载 批量下载
- [x] 分片上传
- [x] 文件夹共享
- [x] 重命名
- [x] 批量下载
- [x] http下载
- [x] 播放历史(已完成，差前端)
- [x] 云存储对接显示（差前端交互）
- [x] 共享文件组的权限管理
- [x] 文件夹操作生成日志(已完成，差前端)
- [ ] 文件夹密码
- [ ] office文件预览
- [ ] torrent下载
- [ ] 容灾备份
***
# 截图
![image](https://github.com/BakaSugus/HomeStorage/blob/main/Picture/首页.jpg)

![image](https://github.com/BakaSugus/HomeStorage/blob/main/Picture/存储位置.jpg)

![image](https://github.com/BakaSugus/HomeStorage/blob/main/Picture/QQ%E6%88%AA%E5%9B%BE20210305001250.jpg)

***
# 使用
 1. 安装jdk
 2. 安装MariaDB 
 3. 安装nginx
 4. 拉取文件(前端的文件就是文件夹dist,后端的jar包在release里)
 6. 修改nginx配置
 ```
 server {
    # 服务器端口
    listen       80;
    # 服务器名称
    server_name  localhost;
    proxy_connect_timeout 600;

    proxy_read_timeout 600;

    proxy_send_timeout 600;
    # 路径配置
    location / {
        #前端打包文件所在目录
        root   /usr/local/dist;
        index  index.html;
        try_files $uri $uri/ @router;  
    }
 }
 ```
 7. 运行jar包
 ```
 nohup java -jar netstorage-0.0.1-SNAPSHOT.jar --ip=192.168.1.121 --aria2=/mnts1/HomeStorage/aria2 --workSpace=/mnts1/HomeStorage/workSpace --server.tomcat.basedir=/mnts1/HomeStorage/tomcat &
 ```
