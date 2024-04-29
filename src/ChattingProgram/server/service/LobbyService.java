package ChattingProgram.server.service;

import ChattingProgram.domain.Client;
import ChattingProgram.domain.Clients;
import ChattingProgram.domain.Command;
import ChattingProgram.domain.Rooms;
import java.io.IOException;
import java.util.StringTokenizer;

public class LobbyService {
    public static void sendCommand(Client me) {
        me.println(Command.allExplain());
    }

    public static boolean isStartWithCommand(String msg, Client me) {
        if (msg.charAt(0) == '/') {
            return true;
        }
        me.println("error : 현재 대화방에 들어가 있지 않습니다\n");
        return false;
    }

    public static boolean isCorrectCommand(Client me, String cmd, StringTokenizer st) {
        if (!isUsableCommand(cmd)) {
            me.println("error : 로비에서 사용 불가한 명령입니다.\n");
            return false;
        }

        if (!isCorrectCommandUse(cmd, st)) {
            me.println("error : 명령어가 잘못 사용되었습니다.\n");
            return false;
        }

        return true;
    }

    private static boolean isUsableCommand(String cmd) {
        return Command.isLobbyCommand(cmd);
    }

    private static boolean isCorrectCommandUse(String cmd, StringTokenizer st) {
        return Command.isCorrectCommandUse(cmd, st);
    }

    public static boolean activeByCommand(Client me, String cmd, StringTokenizer st, Clients allClient, Rooms allRoom)
            throws IOException {

        if ("/bye".equals(cmd)) {
            return false;
        }

        int roomNumber;
        String password = null;
        switch (cmd) {
            case "/list":
                sendRoomList(me, allRoom);
                break;
            case "/create":
                roomNumber = ChatRoomService.makeNewRoom(st, me, allRoom);
                ChatRoomService.enterRoom(me, allRoom, roomNumber, allClient);
                break;
            case "/join":
                try {
                    roomNumber = Integer.parseInt(st.nextToken());
                    if (st.hasMoreTokens()) {
                        password = st.nextToken();
                    }
                    ChatRoomService.joinRoom(me, allRoom, roomNumber, password, allClient);
                } catch (NumberFormatException e) {
                    me.println("error : 명령어가 잘못 사용되었습니다.");
                }
                break;
            case "/users":
                me.println("- 현재 접속 중인 유저 -\n" + allClient + "--------------");
                break;
            case "/toAll":
                allClient.println("[전체 메세지] " + me.getNickName() + " : " + st.nextToken());
        }
        return true;
    }

    private static void sendRoomList(Client me, Rooms allRoom) {
        if (allRoom.isEmpty()) {
            me.println("존재하는 방이 없습니다.\n");
            return;
        }
        me.println("현재 대화방 목록 : " + allRoom.list());

    }
}
