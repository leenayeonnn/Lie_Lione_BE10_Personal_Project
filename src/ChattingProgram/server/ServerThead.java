package ChattingProgram.server;

import ChattingProgram.domain.Client;
import ChattingProgram.domain.Clients;
import ChattingProgram.domain.Room;
import ChattingProgram.domain.Rooms;
import ChattingProgram.server.service.ClientSettingService;
import ChattingProgram.server.service.LobbyService;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

public class ServerThead extends Thread {
    private final Socket socket;
    private final Clients allClient;
    private final Rooms allRoom;
    private Client me;

    public ServerThead(Socket socket, Clients allClient, Rooms allRoom) {
        this.socket = socket;
        this.allClient = allClient;
        this.allRoom = allRoom;
    }

    @Override
    public void run() {
        try {
            me = new Client(socket);
            ClientSettingService.initializeClientNickName(me, allClient);
            ClientSettingService.addToClientList(me, allClient, socket);

            LobbyService.sendCommand(me);

            while (true) {
                String msg;
                if ((msg = me.readLine()).isBlank()) {
                    continue;
                }

                if (!LobbyService.isStartWithCommand(msg, me)) {
                    continue;
                }

                StringTokenizer st = new StringTokenizer(msg);
                String cmd = st.nextToken();

                if (!LobbyService.isCorrectCommand(me, cmd, st)) {
                    continue;
                }

                if (!LobbyService.activeByCommand(me, cmd, st, allClient, allRoom)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(me.getNickName() + " 님이 강제종료 되었습니다");

            int roomWithMe = me.getCurrentRoom();

            if (roomWithMe != 0) {
                Room room = allRoom.find(roomWithMe);
                room.remove(me);

                if (room.isEmpty()) {
                    allRoom.remove(room);
                    System.out.println("방번호 [" + roomWithMe + "]가 삭제되었습니다.");
                } else {
                    room.broadcast(me.getNickName() + "님이 방을 나갔습니다.");
                }
            }
        } finally {
            allClient.remove(me);
            System.out.println(me.getNickName() + " 님과의 연결을 끊습니다.");
            try {
                me.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
