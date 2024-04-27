package ChattingProgram.domain;

public class Room {
    private int roomNumber;
    private Clients participants;

    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
        participants = new Clients();
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void add(Client client) {
        participants.add(client);
    }

    public void broadcast(String msg) {
        participants.println(msg);
    }

    public String getParticipants() {
        return participants.toString();
    }

    public Client findClient(String nickName) {
        return participants.find(nickName);
    }

    public void remove(Client me) {
        participants.remove(me);
    }

    public boolean isEmpty() {
        return participants.isEmpty();
    }

    @Override
    public String toString() {
        return String.valueOf(roomNumber);
    }
}
