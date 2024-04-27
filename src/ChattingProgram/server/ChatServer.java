package ChattingProgram.server;

import ChattingProgram.domain.Clients;
import ChattingProgram.domain.Rooms;
import java.io.IOException;
import java.net.ServerSocket;

public class ChatServer {
    private final static int PORT_NUMBER = 12345;

    public static void main(String[] args) {

        Clients allClient = new Clients();
        Rooms allRoom = new Rooms();

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