package ChattingProgram.server.service;

import ChattingProgram.Command;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.StringTokenizer;

public class LobbyService {
    public static void sendCommand(PrintWriter out) {
        out.println(Command.allExplain());
    }
    
    public static boolean isStartWithCommand(String msg, PrintWriter out) {
        if (msg.charAt(0) == '/') {
            return true;
        }
        out.println("현재 대화방에 들어가 있지 않습니다\n");
        return false;
    }

    public static boolean isCorrectCommand(PrintWriter out, String cmd, StringTokenizer st) {
        if (!isUsableCommand(cmd)) {
            out.println("error : 로비에서 사용 불가한 명령입니다.\n");
            return false;
        }

        if (!isCorrectCommandUse(cmd, st)) {
            out.println("error : 명령어가 잘못 사용되었습니다.\n");
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

    public static boolean activeByCommand(BufferedReader in, PrintWriter out, String cmd, StringTokenizer st,
                                          String nickName, Map<String, PrintWriter> allClient,
                                          Map<Integer, Map<String, PrintWriter>> allRoom) throws IOException {
        if ("/bye".equals(cmd)) {
            return false;
        }

        int roomNumber;
        switch (cmd) {
            case "/list":
                sendRoomList(out, allRoom);
                break;
            case "/create":
                roomNumber = ChatRoomService.makeNewRoom(out, allRoom);
                ChatRoomService.enterRoom(in, out, nickName, allRoom, roomNumber, allClient);
                break;
            case "/join":
                try {
                    roomNumber = Integer.parseInt(st.nextToken());
                    ChatRoomService.joinRoom(in, out, nickName, allRoom, roomNumber, allClient);
                } catch (NumberFormatException e) {
                    out.println("error : 명령어가 잘못 사용되었습니다.");
                }
                break;
            case "/users":
                out.println("현재 접속 중인 유저 : " + allClient.keySet());
                break;
            case "/toAll":
                allClient.values().forEach(pw -> pw.printf("[전체 메세지] %s : %s\n", nickName, st.nextToken()));
        }
        return true;
    }

    private static void sendRoomList(PrintWriter out, Map<Integer, Map<String, PrintWriter>> allRoom) {
        if (allRoom.isEmpty()) {
            out.println("존재하는 방이 없습니다.\n");
            return;
        }
        out.println("현재 대화방 목록 : " + allRoom.keySet());

    }
}
