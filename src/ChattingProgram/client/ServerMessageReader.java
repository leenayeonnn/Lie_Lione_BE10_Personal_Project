package ChattingProgram.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ServerMessageReader implements Runnable {
    private final BufferedReader in;
    private final PrintWriter out;

    public ServerMessageReader(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            String serverLine;
            while ((serverLine = in.readLine()) != null) {
                if ("withdraw".equals(serverLine)) {
                    out.println("/withdraw");
                    continue;
                }
                System.out.println(serverLine); // 서버로부터 받은 메시지를 출력
            }
        } catch (IOException e) {
            System.out.println("Server connection was closed.");
        }
    }
}

