package ChattingProgram.domain;

import java.util.ArrayList;
import java.util.List;

public class Rooms {
    private List<Room> rooms;

    public Rooms() {
        rooms = new ArrayList<>();
    }

    public boolean isEmpty() {
        return rooms.isEmpty();
    }

    public String list() {
        return rooms.toString();
    }

    public void add(int roomNumber) {
        rooms.add(new Room(roomNumber));
    }

    public Room find(int roomNumber) {
        return rooms.stream()
                .filter(room -> room.getRoomNumber() == roomNumber)
                .findFirst()
                .orElse(null);
    }

    public boolean contains(int roomNumber) {
        return rooms.stream()
                .anyMatch(room -> room.getRoomNumber() == roomNumber);
    }

    public synchronized void remove(Room room) {
        rooms.remove(room);
    }
}
