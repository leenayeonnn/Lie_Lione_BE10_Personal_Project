package ChattingProgram.server.service;

import ChattingProgram.domain.Client;
import ChattingProgram.domain.Clients;
import java.io.IOException;
import java.net.Socket;

public class ClientSettingService {
    public static void initializeClientNickName(Client me, Clients allClient) throws IOException {
        String nicknName;
        while ((nicknName = me.readLine()) != null) {
            if (nicknName.isBlank()) {
                me.println("nameBlank");
            } else if (allClient.contains(nicknName)) {
                me.println("nameDuplicate");
            } else {
                break;
            }
        }

        me.setNickName(nicknName);
    }

    public static void addToClientList(Client me, Clients allClient, Socket socket) {
        allClient.add(me);
        me.println("[" + me.getNickName() + "]님 어서오세요\n");
        System.out.printf("%s 닉네임의 사용자가 연결했습니다 : %s\n", me.getNickName(), socket.getInetAddress().getHostAddress());
    }
}
