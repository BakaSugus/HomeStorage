
import Downloader.DownloadManager;
import socket.SocketService;

public class Main {

//    private static DownloadManager downloadManager = DownloadManager.getInstance();

    public static void main(String[] args) {

        new SocketService(
                args.length == 0 ?
                        6888 : Integer.parseInt(args[0])
        );
    }
}
