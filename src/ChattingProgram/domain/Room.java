package ChattingProgram.domain;

public class Room {
    private final int roomNumber;
    private final Clients participants;

    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
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

    public void broadcast(String msg) {
        participants.println(msg);
    }

    @Override
    public String toString() {
        return String.valueOf(roomNumber);
    }
}
