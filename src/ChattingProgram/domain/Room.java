package ChattingProgram.domain;

public class Room {
    private final int roomNumber;
    private String password;
    private final Clients participants;

    public Room(int roomNumber, String password) {
        this.roomNumber = roomNumber;
        this.password = password;
        participants = new Clients();
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getParticipants() {
        return participants.toString();
    }

    public Client findClient(String nickName) {
        return participants.find(nickName);
    }

    public void add(Client client) {
        participants.add(client);
    }

    public void remove(Client me) {
        participants.remove(me);
    }

    public boolean isEmpty() {
        return participants.isEmpty();
    }

    public boolean isPrivate() {
        return password != null;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void broadcast(String msg) {
        participants.println(msg);
    }

    @Override
    public String toString() {
        return String.valueOf(roomNumber);
    }
}
