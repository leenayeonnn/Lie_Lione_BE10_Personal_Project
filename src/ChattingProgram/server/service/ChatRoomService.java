package ChattingProgram.server.service;

import ChattingProgram.domain.Client;
import ChattingProgram.domain.Clients;
import ChattingProgram.domain.Command;
import ChattingProgram.domain.Room;
import ChattingProgram.domain.Rooms;
import java.io.IOException;
import java.util.StringTokenizer;

public class ChatRoomService {
    private static int newRoomNumber;

    public synchronized static int makeNewRoom(StringTokenizer st, Client me, Rooms allRoom) {
        if (allRoom.isEmpty()) {
            newRoomNumber = 0;
        }

        String password = null;
        if (st.hasMoreTokens()) {
            password = st.nextToken();
        }

        allRoom.add(++newRoomNumber, password);

        me.println("방 번호 [" + newRoomNumber + "] 가 생성되었습니다.\n");
        System.out.println("방 번호 [" + newRoomNumber + "] 가 생성되었습니다.");

        return newRoomNumber;
    }

    public static void enterRoom(Client me, Rooms allRoom, int roomNumber, Clients allClient) throws IOException {
        Room currentRoom = allRoom.find(roomNumber);

        entrance(currentRoom, roomNumber, me);
        boolean isExit = chatting(currentRoom, me, allClient);
        exitRoom(currentRoom, roomNumber, allRoom, me, isExit);
    }

    private synchronized static void entrance(Room currentRoom, int roomNumber, Client me) {
        currentRoom.add(me);
        me.enterRoom(roomNumber);
        me.println("- 대화방 [" + roomNumber + "] -");
        currentRoom.broadcast(me.getNickName() + " 님이 방에 입장했습니다.");
    }

    private static boolean chatting(Room currentRoom, Client me, Clients allClient) throws IOException {

        while (true) {
            String msg;
            if ((msg = me.readLine()).isBlank()) {
                continue;
            }

            if (!isStartWithCommand(msg)) {
                currentRoom.broadcast(me.getNickName() + " : " + msg);
                continue;
            }

            StringTokenizer st = new StringTokenizer(msg);
            String cmd = st.nextToken();

            if (!isCorrectCommand(me, cmd, st)) {
                continue;
            }

            int exitCase;
            if ((exitCase = activeByCommand(cmd, st, me, currentRoom, allClient)) != 0) {
                return exitCase == 1;
            }
        }
    }

    private static boolean isStartWithCommand(String msg) {
        return msg.charAt(0) == '/';
    }

    public static boolean isCorrectCommand(Client me, String cmd, StringTokenizer st) {
        if (!isUsableCommand(cmd)) {
            me.println("error : 대화방에서 사용 불가한 명령입니다.\n");
            return false;
        }

        if (!isCorrectCommandUse(cmd, st)) {
            me.println("error : 명령어가 잘못 사용되었습니다.\n");
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

    private static int activeByCommand(String cmd, StringTokenizer st, Client me, Room currentRoom,
                                       Clients allClient) {
        if ("/exit".equals(cmd)) {
            return 1;
        }

        if ("/withdraw".equals(cmd)) {
            return -1;
        }

        switch (cmd) {
            case "/users":
                me.println("- 현재 접속 중인 유저 -\n" + allClient + "--------------");
                break;
            case "/roomUsers":
                me.println("- 현재 방에 있는 유저 -\n" + currentRoom.getParticipants() + "-------------");
                break;
            case "/whisper":
                whisper(st, me, currentRoom);
                break;
            case "/toAll":
                allClient.println("[전체 메세지] " + me.getNickName() + " : " + st.nextToken());
            case "/report":
                report(st, me, currentRoom);
        }
        return 0;
    }

    private static void whisper(StringTokenizer st, Client me, Room currentRoom) {
        String whisperNickname = st.nextToken();

        if (whisperNickname.equals(me.getNickName())) {
            me.println("error : 본인에게 귓속말은 불가합니다.");
            return;
        }

        Client whisperClient;
        if ((whisperClient = currentRoom.findClient(whisperNickname)) == null) {
            me.println("error : 존재하지 않는 닉네임 입니다.");
            return;
        }

        String msg = st.nextToken();
        me.println("[귓속말 -> " + whisperNickname + "] " + me.getNickName() + " : " + msg);
        whisperClient.println("[귓속말] " + me.getNickName() + " : " + msg);
    }

    private static void report(StringTokenizer st, Client me, Room currentRoom) {
        String reportNicknaem = st.nextToken();

        if (reportNicknaem.equals(me.getNickName())) {
            me.println("error : 본인에게 신고는 불가합니다.");
            return;
        }

        Client reportClient;
        if ((reportClient = currentRoom.findClient(reportNicknaem)) == null) {
            me.println("error : 현재 대화방에 존재하지 않는 닉네임 입니다.");
            return;
        }

        reportClient.receiveReport();
        if (reportClient.getReport() == 3) {
            reportClient.println("신고를 받았습니다 (3/3)");
            reportClient.println("withdraw");
            return;
        }

        reportClient.println("신고를 받았습니다 (" + reportClient.getReport() + "/3)");

    }

    private synchronized static void exitRoom(Room currentRoom, int roomNumber, Rooms allRoom, Client me,
                                              boolean isExit) {
        currentRoom.remove(me);
        me.exitRoom();
        if (currentRoom.isEmpty()) {
            allRoom.remove(currentRoom);
            System.out.println("방번호 [" + roomNumber + "]가 삭제되었습니다.");
            me.println("방을 나왔습니다.\n");
            return;
        }

        if (isExit) {
            currentRoom.broadcast(me.getNickName() + "님이 방을 나갔습니다.");
            me.println("방을 나왔습니다.\n");
        } else {
            currentRoom.broadcast(me.getNickName() + "님이 강제 퇴장 되었습니다.");
            me.println("강제 퇴장 되었습니다.\n");
        }
    }

    public static void joinRoom(Client me, Rooms allRoom, int roomNumber, String password, Clients allClient)
            throws IOException {
        if (!allRoom.contains(roomNumber)) {
            me.println("error : 해당 방이 존재하지 않습니다.\n");
            return;
        }

        Room wantRoom = allRoom.find(roomNumber);
        if (wantRoom.isPrivate()) {
            if (!checkPassword(me, wantRoom, password)) {
                return;
            }
        } else {
            if (password != null) {
                me.println("error : 해당 방은 비밀방이 아닙니다.\n");
                return;
            }
        }

        enterRoom(me, allRoom, roomNumber, allClient);
    }

    private static boolean checkPassword(Client me, Room wantRoom, String password) {
        if (wantRoom.checkPassword(password)) {
            return true;
        }
        me.println("error : 비밀번호가 틀렸습니다.\n");
        return false;
    }
}
