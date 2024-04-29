package ChattingProgram.domain;

import java.util.ArrayList;
import java.util.List;

public class Rooms {
    private final List<Room> rooms;

    public Rooms() {
        rooms = new ArrayList<>();
    }

    public boolean contains(int roomNumber) {
        return rooms.stream()
                .anyMatch(room -> room.getRoomNumber() == roomNumber);
    }

    public Room find(int roomNumber) {
        return rooms.stream()
                .filter(room -> room.getRoomNumber() == roomNumber)
                .findFirst()
                .orElse(null);
    }

    public void add(int roomNumber, String password) {
        rooms.add(new Room(roomNumber, password));
    }

    public synchronized void remove(Room room) {
        rooms.remove(room);
    }

    public boolean isEmpty() {
        return rooms.isEmpty();
    }

    public String list() {
        return rooms.toString();
    }
}
