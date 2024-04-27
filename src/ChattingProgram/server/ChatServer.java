package ChattingProgram.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private final static int PORT_NUMBER = 12345;

    public static void main(String[] args) {

        Map<String, PrintWriter> allClient = new HashMap<>();
        Map<Integer, Map<String, PrintWriter>> allRoom = new HashMap<>();

        try (ServerSocket serverSocket = new ServerSocket(PORT_NUMBER)) {
            System.out.println("서버 준비 완료");

            while (true) {
                new ServerThead(serverSocket.accept(), allClient, allRoom).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}