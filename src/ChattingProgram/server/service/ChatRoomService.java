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

    public synchronized static int makeNewRoom(Client me, Rooms allRoom) {
        if (allRoom.isEmpty()) {
            newRoomNumber = 0;
        }
        allRoom.add(++newRoomNumber);

        me.println("방 번호 [" + newRoomNumber + "] 가 생성되었습니다.\n");
        System.out.println("방 번호 [" + newRoomNumber + "] 가 생성되었습니다.");

        return newRoomNumber;
    }

    public static void enterRoom(Client me, Rooms allRoom, int roomNumber, Clients allClient) throws IOException {
        Room currentRoom = allRoom.find(roomNumber);

        entrance(currentRoom, roomNumber, me);
        chatting(currentRoom, me, allClient);
        exitRoom(currentRoom, roomNumber, allRoom, me);
    }

    private synchronized static void entrance(Room currentRoom, int roomNumber, Client me) {
        currentRoom.add(me);
        me.enterRoom(roomNumber);
        me.println("- 대화방 [" + roomNumber + "] -");
        currentRoom.broadcast(me.getNickName() + " 님이 방에 입장했습니다.");
    }

    private static void chatting(Room currentRoom, Client me, Clients allClient) throws IOException {

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

            if (!activeByCommand(cmd, st, me, currentRoom, allClient)) {
                break;
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

    private static boolean activeByCommand(String cmd, StringTokenizer st, Client me, Room currentRoom,
                                           Clients allClient) {
        if ("/exit".equals(cmd)) {
            return false;
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
        }
        return true;
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

    private synchronized static void exitRoom(Room currentRoom, int roomNumber, Rooms allRoom, Client me) {
        currentRoom.remove(me);
        me.exitRoom();
        if (currentRoom.isEmpty()) {
            allRoom.remove(currentRoom);
            System.out.println("방번호 [" + roomNumber + "]가 삭제되었습니다.");
            return;
        }

        currentRoom.broadcast(me.getNickName() + "님이 방을 나갔습니다.");
    }

    public static void joinRoom(Client me, Rooms allRoom, int roomNumber, Clients allClient) throws IOException {
        if (!allRoom.contains(roomNumber)) {
            me.println("error : 해당 방이 존재하지 않습니다.");
            return;
        }

        enterRoom(me, allRoom, roomNumber, allClient);
    }
}
