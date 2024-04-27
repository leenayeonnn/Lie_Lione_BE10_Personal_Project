package ChattingProgram.server;

import ChattingProgram.server.service.ClientSettingService;
import ChattingProgram.server.service.LobbyService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;

public class ServerThead extends Thread {
    private final Socket socket;
    private final Map<String, PrintWriter> allClient;
    private final Map<Integer, Map<String, PrintWriter>> allRoom;
    private String nickName;

    public ServerThead(Socket socket, Map<String, PrintWriter> allClient,
                       Map<Integer, Map<String, PrintWriter>> allRoom) {
        this.socket = socket;
        this.allClient = allClient;
        this.allRoom = allRoom;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            nickName = ClientSettingService.initializeClientNickName(in, out, allClient);
            ClientSettingService.addToClientList(allClient, nickName, out, socket);

            LobbyService.sendCommand(out);

            while (true) {
                String msg;
                if ((msg = in.readLine()).isBlank()) {
                    continue;
                }

                if (!LobbyService.isStartWithCommand(msg, out)) {
                    break;
                }

                StringTokenizer st = new StringTokenizer(msg);
                String cmd = st.nextToken();

                if (!LobbyService.isCorrectCommand(out, cmd, st)) {
                    continue;
                }

                if (!LobbyService.activeByCommand(in, out, cmd, st, nickName, allClient, allRoom)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(nickName + " 님이 강제종료 되었습니다");

            int roomWithNickName = allRoom.keySet().stream()
                    .filter(roomNumber -> allRoom.get(roomNumber).containsKey(nickName))
                    .findFirst()
                    .orElse(0);

            if (roomWithNickName != 0) {
                Map<String, PrintWriter> room = allRoom.get(roomWithNickName);
                synchronized (room) {
                    room.remove(nickName);
                }

                if (room.isEmpty()) {
                    synchronized (allRoom) {
                        allRoom.remove(roomWithNickName);
                        System.out.println("방번호 [" + roomWithNickName + "]가 삭제되었습니다.");
                    }
                } else {
                    room.values().forEach(pw -> pw.println(nickName + "님이 방을 나갔습니다."));
                }
            }
        } finally {
            synchronized (allClient) {
                allClient.remove(nickName);
            }
        }
    }

}
