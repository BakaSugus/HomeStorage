# 介绍

这个项目是一个支持本地存储和云存储,拥有简单的多用户，功能还算齐全,可以在局域网部署，也可以在公网部署的网盘系统

# 更新
1. 新增了外部文件配置
2. 重写了腾讯对象存储的接口，优化使用逻辑
3. 新增了隐藏文件驱动器，共享文件驱动器，自动导入驱动器
4. 重写了上传逻辑，新增了操作日志
5. 重写了界面！！！！
6. 重写了初始化的各种细节

***

# 功能
- [x] 上传，下载文件
- [x] 多用户 
- [x] 基本权限 
- [x] 基本文件预览
- [x] 压缩下载 批量下载
- [x] 分片上传
- [x] 文件夹共享
- [x] 重命名
- [x] 批量下载
- [x] 腾讯Cos对接
- [x] 阿里云Oss对接
- [x] 操作日志生成
- [x] 自动导入功能
- [x] 隐藏文件和显示文件
- [x] 驱动器迁移(配置文件把文件夹路径改了即可,里面的文件和分区表信息会在初始化的时候进行迁移)
- [ ] 邮件通知
- [ ] 接上OneDrive的大腿
- [ ] 录播
- [ ] 分享
- [ ] 转码
- [ ] 文件属性预览
***
# 截图
![image](https://github.com/BakaSugus/HomeStorage/blob/main/Picture/本地存储.jpg)

![image](https://github.com/BakaSugus/HomeStorage/blob/main/Picture/Cos.jpg)

![image](https://github.com/BakaSugus/HomeStorage/blob/main/Picture/存储驱动器.jpg)

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
 6. 新建json文件(最简单的配置) DEVICE可以不写 会默认生成
 ```
 {
		"ADMIN":{
		"user":"admin",
		"password":"admin",
		"nickname":"admin"
	},
	"DEVICE":{
		"Music" : "C:/Users/Shinelon/Documents/GitHub/HomeStorage/WorkSpace/Music",
		"Video" : "C:/Users/Shinelon/Documents/GitHub/HomeStorage/WorkSpace/Video",
		"Other" : "C:/Users/Shinelon/Documents/GitHub/HomeStorage/WorkSpace/Other",
		"Document" : "C:/Users/Shinelon/Documents/GitHub/HomeStorage/WorkSpace/Document",
		"Folder" : "C:/Users/Shinelon/Documents/GitHub/HomeStorage/WorkSpace/Folder",
		"Picture" : "C:/Users/Shinelon/Documents/GitHub/HomeStorage/WorkSpace/Picture",
		"PDF":"C:/Users/Shinelon/Documents/GitHub/HomeStorage/WorkSpace/PDF",
		"Common":"C:/Users/Shinelon/Documents/GitHub/HomeStorage/WorkSpace/Common",
		"Torrent":"C:/Users/Shinelon/Documents/GitHub/HomeStorage/WorkSpace/Torrent",
		"Software":"C:/Users/Shinelon/Documents/GitHub/HomeStorage/WorkSpace/Software",
		"AndroidSoftware":"C:/Users/Shinelon/Documents/GitHub/HomeStorage/WorkSpace/AndroidSoftware",
		"AutoImport":"C:/Users/Shinelon/Documents/GitHub/HomeStorage/WorkSpace/AutoImport",
		"RAR":"C:/Users/Shinelon/Documents/GitHub/HomeStorage/WorkSpace/RAR",
		"Set":"C:/Users/Shinelon/Documents/GitHub/HomeStorage/WorkSpace/Set",
		"Temp":"C:/Users/Shinelon/Documents/GitHub/HomeStorage/WorkSpace/Temp"
	}
}
 ```

 7. 运行jar包
 ```
 nohup java -jar netstorage-0.0.1-SNAPSHOT.jar --configPath=上面json文件的位置 --spring.datasource.url=你的数据库链接 --spring.datasource.username=用户名 --spring.datasource.password=数据库密码 &
 ```
