package socket;

import socket.Request;
import socket.Response;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.stream.Collectors;

public class SocketMessage {
    public Request SocketMessageReceive(InputStream inputStream) {
        String result = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining(System.lineSeparator()));
        return getRequest(result);
    }

    public String SocketMessageSend(OutputStream outputStream, Response response) {
        return null;
    }

    private Request getRequest(String content) {
        return new Request(content);
    }

    private Response response(){
        return null;
    }
}
