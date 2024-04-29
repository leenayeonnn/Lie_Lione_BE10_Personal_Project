package ChattingProgram.domain;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Closeable {
    private String nickName;
    private int currentRoom;
    private int report;
    private final BufferedReader in;
    private final PrintWriter out;

    public Client(Socket socket) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nicknName) {
        this.nickName = nicknName;
    }

    public int getCurrentRoom() {
        return currentRoom;
    }

    public int getReport() {
        return report;
    }

    public String readLine() throws IOException {
        return in.readLine();
    }

    public void println(String msg) {
        out.println(msg);
    }

    public void enterRoom(int roomNumber) {
        this.currentRoom = roomNumber;
    }

    public void exitRoom() {
        this.currentRoom = 0;
        this.report = 0;
    }

    public void receiveReport() {
        report++;
    }

    @Override
    public String toString() {
        return nickName;
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
    }
}
