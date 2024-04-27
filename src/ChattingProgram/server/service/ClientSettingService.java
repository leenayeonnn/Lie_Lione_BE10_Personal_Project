package ChattingProgram.server.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ClientSettingService {
    public static String initializeClientNickName(BufferedReader in, PrintWriter out,
                                                  Map<String, PrintWriter> allClient) throws IOException {
        String nickname;
        while ((nickname = in.readLine()) != null) {
            if (nickname.isBlank()) {
                out.println("nameBlank");
            } else if (allClient.containsKey(nickname)) {
                out.println("nameDuplicate");
            } else {
                break;
            }
        }
        return nickname;
    }

    public static void addToClientList(Map<String, PrintWriter> allClient, String nickName, PrintWriter out,
                                       Socket socket) {
        synchronized (allClient) {
            allClient.put(nickName, out);
        }
        out.println("[" + nickName + "]님 어서오세요\n");
        System.out.printf("%s 닉네임의 사용자가 연결했습니다 : %s\n", nickName, socket.getInetAddress().getHostAddress());
    }
}
