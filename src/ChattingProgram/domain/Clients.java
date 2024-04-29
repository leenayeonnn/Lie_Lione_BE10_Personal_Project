package ChattingProgram.domain;

import java.util.ArrayList;
import java.util.List;

public class Clients {
    private final List<Client> clients;

    public Clients() {
        clients = new ArrayList<>();
    }

    public boolean contains(String nickName) {
        return clients.stream()
                .anyMatch(client -> client.getNickName().equals(nickName));
    }

    public Client find(String nickName) {
        return clients.stream()
                .filter(client -> client.getNickName().equals(nickName))
                .findFirst()
                .orElse(null);
    }

    public synchronized void add(Client client) {
        clients.add(client);
    }

    public synchronized void remove(Client me) {
        clients.remove(me);
    }

    public boolean isEmpty() {
        return clients.isEmpty();
    }

    public void println(String msg) {
        clients.forEach(client -> client.println(msg));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        clients.forEach(client -> sb.append(client).append("\n"));
        return sb.toString();
    }
}
