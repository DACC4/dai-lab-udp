package ch.heigvd;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Auditor {
    private final int port = 2205;
    private final Map<String, Musician> activeMusicians;


    public Auditor() {
        activeMusicians = new ConcurrentHashMap<>();
    }

    public void run(){
        startServer();
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Auditor Server is listening on port " + port);
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("New client connected");
                    handleClient(socket);
                } catch (IOException e) {
                    System.out.println("Client exception: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }

    private void handleClient(Socket socket) throws IOException {
        var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));

        out.write(getActiveMusiciansJSON());
        out.newLine();
        out.flush();
    }

    private String getActiveMusiciansJSON() {
        long currentTime = System.currentTimeMillis();
        activeMusicians.entrySet().removeIf(e -> currentTime - e.getValue().lastActivity > 5000);

        return new Gson().toJson(activeMusicians.values());
    }

    // Method to update musician activity (to be called when a sound is played)
    public void updateMusician(String uuid, String instrument) {
        activeMusicians.put(uuid, new Musician(uuid, instrument, System.currentTimeMillis()));
    }
}
