package cn.j.netstorage.Config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.List;
import java.util.Properties;

@Component
public class CheckEnv implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        if (args.containsOption("aria2")) {
//            List<String> list = args.getOptionValues("aria2");
//            if (!list.isEmpty())
//                aria2Run(list.get(0));
//        }
    }

    public void aria2Run(String path) {
//        if (!StringUtils.hasText(path)) return;
//
//        File conf = new File(path + "/aria2.conf");
//        File session = new File(path + "/aria2.session");
//
//
//        if (!conf.exists()) {
//            try {
//                Properties props = System.getProperties(); //获得系统属性集
//                String osName = props.getProperty("os.name");
//                switch (osName) {
//                    case "linux":
//                        writeFiles(conf, String.format(Aria2Conf, "complete.sh"));
//                        writeFiles(new File(path + "/complete.sh"), this.linux_complete);
//                        break;
//                    case "window":
//                        writeFiles(conf, String.format(Aria2Conf, "complete.bat"));
//                        writeFiles(new File(path + "/complete.bat"), this.window_complete);
//                        break;
//                }
//                session.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        System.out.println(conf.getAbsoluteFile());
//        String cmd = "aria2c --conf-path=" + conf.getAbsolutePath();
//        try {
//            Process process = Runtime.getRuntime().exec(cmd);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private void writeFiles(File file, String content) {
        if (file.exists()) return;

        byte bytes[] = new byte[512];
        bytes = content.getBytes();
        int b = bytes.length;
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes, 0, b);
            fos.write(bytes);
            fos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private final String linux_complete = "";
    private final String window_complete = "";


    private final String Aria2Conf = "## '#'开头为注释内容, 选项都有相应的注释说明, 根据需要修改 ##\n" +
            "## 被注释的选项填写的是默认值, 建议在需要修改时再取消注释  ##\n" +
            "\n" +
            "## 文件保存相关 ##\n" +
            "\n" +
            "# 文件的保存路径(可使用绝对路径或相对路径), 默认: 当前启动位置\n" +
            "dir=Download\n" +
            "# 启用磁盘缓存, 0为禁用缓存, 需1.16以上版本, 默认:16M\n" +
            "disk-cache=32M\n" +
            "# 文件预分配方式, 能有效降低磁盘碎片, 默认:prealloc\n" +
            "# 预分配所需时间: none < falloc < trunc < prealloc\n" +
            "# NTFS建议使用falloc\n" +
            "file-allocation=none\n" +
            "# 断点续传\n" +
            "continue=true\n" +
            "\n" +
            "## 下载连接相关 ##\n" +
            "\n" +
            "# 最大同时下载任务数, 运行时可修改, 默认:5\n" +
            "max-concurrent-downloads=10\n" +
            "# 同一服务器连接数, 添加时可指定, 默认:1\n" +
            "max-connection-per-server=5\n" +
            "# 最小文件分片大小, 添加时可指定, 取值范围1M -1024M, 默认:20M\n" +
            "# 假定size=10M, 文件为20MiB 则使用两个来源下载; 文件为15MiB 则使用一个来源下载\n" +
            "min-split-size=10M\n" +
            "# 单个任务最大线程数, 添加时可指定, 默认:5\n" +
            "split=20\n" +
            "# 整体下载速度限制, 运行时可修改, 默认:0\n" +
            "#max-overall-download-limit=0\n" +
            "# 单个任务下载速度限制, 默认:0\n" +
            "#max-download-limit=0\n" +
            "# 整体上传速度限制, 运行时可修改, 默认:0\n" +
            "max-overall-upload-limit=1M\n" +
            "# 单个任务上传速度限制, 默认:0\n" +
            "#max-upload-limit=1000\n" +
            "# 禁用IPv6, 默认:false\n" +
            "disable-ipv6=false\n" +
            "\n" +
            "## 进度保存相关 ##\n" +
            "\n" +
            "# 从会话文件中读取下载任务\n" +
            "input-file=aria2.session\n" +
            "# 在Aria2退出时保存`错误/未完成`的下载任务到会话文件\n" +
            "save-session=aria2.session\n" +
            "# 定时保存会话, 0为退出时才保存, 需1.16.1以上版本, 默认:0\n" +
            "#save-session-interval=60\n" +
            "\n" +
            "## RPC相关设置 ##\n" +
            "\n" +
            "# 启用RPC, 默认:false\n" +
            "enable-rpc=true\n" +
            "# 允许所有来源, 默认:false\n" +
            "rpc-allow-origin-all=true\n" +
            "# 允许非外部访问, 默认:false\n" +
            "rpc-listen-all=true\n" +
            "# 事件轮询方式, 取值:[epoll, kqueue, port, poll, select], 不同系统默认值不同\n" +
            "#event-poll=select\n" +
            "# RPC监听端口, 端口被占用时可以修改, 默认:6800\n" +
            "#rpc-listen-port=6800\n" +
            "# 设置的RPC授权令牌, v1.18.4新增功能, 取代 --rpc-user 和 --rpc-passwd 选项\n" +
            "#rpc-secret=mivm.cn\n" +
            "# 设置的RPC访问用户名, 此选项新版已废弃, 建议改用 --rpc-secret 选项\n" +
            "#rpc-user=<USER>\n" +
            "# 设置的RPC访问密码, 此选项新版已废弃, 建议改用 --rpc-secret 选项\n" +
            "#rpc-passwd=<PASSWD>\n" +
            "\n" +
            "## BT/PT下载相关 ##\n" +
            "\n" +
            "# 当下载的是一个种子(以.torrent结尾)时, 自动开始BT任务, 默认:true\n" +
            "follow-torrent=true\n" +
            "# BT监听端口, 当端口被屏蔽时使用, 默认:6881-6999\n" +
            "listen-port=51413\n" +
            "# 单个种子最大连接数, 默认:55\n" +
            "#bt-max-peers=55\n" +
            "# 打开DHT功能, PT需要禁用, 默认:true\n" +
            "enable-dht=true\n" +
            "# 打开IPv6 DHT功能, PT需要禁用\n" +
            "#enable-dht6=false\n" +
            "# DHT网络监听端口, 默认:6881-6999\n" +
            "#dht-listen-port=6881-6999\n" +
            "# 本地节点查找, PT需要禁用, 默认:false\n" +
            "#bt-enable-lpd=true\n" +
            "# 种子交换, PT需要禁用, 默认:true\n" +
            "enable-peer-exchange=true\n" +
            "# 每个种子限速, 对少种的PT很有用, 默认:50K\n" +
            "#bt-request-peer-speed-limit=50K\n" +
            "# 客户端伪装, PT需要\n" +
            "peer-id-prefix=-TR2770-\n" +
            "user-agent=Transmission/2.77\n" +
            "# 当种子的分享率达到这个数时, 自动停止做种, 0为一直做种, 默认:1.0\n" +
            "seed-ratio=0.1\n" +
            "# 强制保存会话, 即使任务已经完成, 默认:false\n" +
            "# 较新的版本开启后会在任务完成后依然保留.aria2文件\n" +
            "#force-save=false\n" +
            "# BT校验相关, 默认:true\n" +
            "#bt-hash-check-seed=true\n" +
            "# 继续之前的BT任务时, 无需再次校验, 默认:false\n" +
            "bt-seed-unverified=true\n" +
            "# 保存磁力链接元数据为种子文件(.torrent文件), 默认:false\n" +
            "#bt-save-metadata=true\n" +
            "on-download-complete=%s";
}
