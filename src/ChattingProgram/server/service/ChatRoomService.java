package ChattingProgram.server.service;

import ChattingProgram.Command;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class ChatRoomService {
    private static int newRoomNumber;

    public synchronized static int makeNewRoom(PrintWriter out, Map<Integer, Map<String, PrintWriter>> allRoom) {
        if (allRoom.isEmpty()) {
            newRoomNumber = 0;
        }
        allRoom.put(++newRoomNumber, new HashMap<>());

        out.println("방 번호 [" + newRoomNumber + "] 가 생성되었습니다.\n");
        System.out.println("방 번호 [" + newRoomNumber + "] 가 생성되었습니다.");

        return newRoomNumber;
    }

    public static void enterRoom(BufferedReader in, PrintWriter out, String nickName,
                                 Map<Integer, Map<String, PrintWriter>> allRoom, int roomNumber,
                                 Map<String, PrintWriter> allClient) throws IOException {
        Map<String, PrintWriter> currentRoom = allRoom.get(roomNumber);

        entrance(currentRoom, roomNumber, nickName, out);
        chatting(currentRoom, nickName, allClient, in, out);
        exitRoom(currentRoom, roomNumber, allRoom, nickName);
    }

    private synchronized static void entrance(Map<String, PrintWriter> currentRoom, int roomNumber, String nickName,
                                              PrintWriter out) {
        currentRoom.put(nickName, out);
        out.printf("- 대화방 [%d] -\n", roomNumber);
        currentRoom.values().forEach(pw -> pw.println(nickName + " 님이 방에 입장했습니다."));
    }

    private static void chatting(Map<String, PrintWriter> currentRoom, String nickName,
                                 Map<String, PrintWriter> allClient, BufferedReader in,
                                 PrintWriter out) throws IOException {

        while (true) {
            String msg;
            if ((msg = in.readLine()).isBlank()) {
                continue;
            }

            if (!isStartWithCommand(msg)) {
                currentRoom.values().forEach(pw -> pw.println(nickName + " : " + msg));
                continue;
            }

            StringTokenizer st = new StringTokenizer(msg);
            String cmd = st.nextToken();

            if (!isCorrectCommand(out, cmd, st)) {
                continue;
            }

            if (!activeByCommand(out, cmd, st, currentRoom, nickName, allClient)) {
                break;
            }
        }
    }

    private static boolean isStartWithCommand(String msg) {
        return msg.charAt(0) == '/';
    }

    public static boolean isCorrectCommand(PrintWriter out, String cmd, StringTokenizer st) {
        if (!isUsableCommand(cmd)) {
            out.println("error : 대화방에서 사용 불가한 명령입니다.\n");
            return false;
        }

        if (!isCorrectCommandUse(cmd, st)) {
            out.println("error : 명령어가 잘못 사용되었습니다.\n");
            return false;
        }

        return true;
    }

    private static boolean isUsableCommand(String cmd) {
        return Command.isRoomCommand(cmd);
    }

    private static boolean isCorrectCommandUse(String cmd, StringTokenizer st) {
        return Command.isCorrectCommandUse(cmd, st);
    }

    private static boolean activeByCommand(PrintWriter out, String cmd, StringTokenizer st,
                                           Map<String, PrintWriter> currentRoom, String nickName,
                                           Map<String, PrintWriter> allClient) {
        if ("/exit".equals(cmd)) {
            return false;
        }

        switch (cmd) {
            case "/users":
                out.println("현재 접속 중인 유저 : " + allClient.keySet());
                break;
            case "/roomUsers":
                out.println("현재 방에 있는 유저 : " + currentRoom.keySet());
                break;
            case "/whisper":
                whisper(st, out, nickName, currentRoom);
                break;
            case "/toAll":
                allClient.values().forEach(pw -> pw.printf("[전체 메세지] %s : %s\n", nickName, st.nextToken()));
        }
        return true;
    }

    private static void whisper(StringTokenizer st, PrintWriter out, String nickName,
                                Map<String, PrintWriter> currentRoom) {
        String whisperNickname = st.nextToken();

        if (whisperNickname.equals(nickName)) {
            out.println("error : 본인에게 귓속말은 불가합니다.");
            return;
        }

        if (!currentRoom.containsKey(whisperNickname)) {
            out.println("error : 존재하지 않는 닉네임 입니다.");
            return;
        }

        String msg = st.nextToken();
        out.printf("[귓속말 -> %s] %s : %s\n", whisperNickname, nickName, msg);
        currentRoom.get(whisperNickname).printf("[귓속말] %s : %s\n", nickName, msg);
    }

    private synchronized static void exitRoom(Map<String, PrintWriter> currentRoom, int roomNumber,
                                              Map<Integer, Map<String, PrintWriter>> allRoom, String nickName) {
        currentRoom.remove(nickName);
        if (currentRoom.isEmpty()) {
            allRoom.remove(roomNumber);
            System.out.println("방번호 [" + roomNumber + "]가 삭제되었습니다.");
            return;
        }

        currentRoom.values().forEach(pw -> pw.println(nickName + "님이 방을 나갔습니다."));
    }

    public static void joinRoom(BufferedReader in, PrintWriter out, String nickName,
                                Map<Integer, Map<String, PrintWriter>> allRoom, int roomNumber,
                                Map<String, PrintWriter> allClient)
            throws IOException {
        if (!allRoom.containsKey(roomNumber)) {
            out.println("error : 해당 방이 존재하지 않습니다.");
            return;
        }

        enterRoom(in, out, nickName, allRoom, roomNumber, allClient);
    }
}
