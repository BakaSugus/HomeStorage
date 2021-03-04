package socket;

import Downloader.DownloadManager;
import Parse.Video;
import dlna.DLNAService;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.List;

public class SocketService {
    public SocketService(int port) {
        this.StartSocketServer(port);
    }

    public void StartSocketServer(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            while (true) {
                Socket client = socket.accept();
                new RunOnOtherThread(client);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public class RunOnOtherThread implements Runnable {
        private Socket socket;
        private SocketMessage socketMessage = new SocketMessage();
        private DownloadManager downloadManager=DownloadManager.getInstance();
        public RunOnOtherThread(Socket socket) {
            this.socket = socket;
            new Thread(this).start();
        }

        @Override
        public void run() {
            try {
                Request request = socketMessage.SocketMessageReceive(socket.getInputStream());
                String type = request.getTYPE();
                switch (type.toLowerCase()) {
                    case "download":downloadManager.download(request);
                        break;
                    case "dlna":new DLNAService(request).searchAndConn();
                        break;
                    case "download_list":
                        List<String> list=downloadManager.getData(request);
//                        System.out.println(list);
//                        socket.getOutputStream().write(list.get);
                        break;
                    case "parse_download"://先解析 然后下载
                        break;
                    case "parse_video":
                        break;
                }
            } catch (IOException ex) {

                ex.printStackTrace();
            }
        }
    }
}
