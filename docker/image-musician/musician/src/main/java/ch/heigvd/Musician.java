package ch.heigvd;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Timer;
import java.util.TimerTask;

import static java.nio.charset.StandardCharsets.*;

class Musician {
    final static String IPADDRESS = "239.255.22.5";
    final static int PORT = 9904;

    public static void main(String[] args) {
        MusicianMessage musicianMessage = null;
        try {
            String instrument = args[0];
            musicianMessage = new MusicianMessage(instrument);
        } catch (Exception e) {
            System.out.println("Usage: docker run -d dai/musician <instrument>");
            return;
        }

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String message = gson.toJson(musicianMessage);

        byte[] payload = message.getBytes(UTF_8);

        // Send the payload every second
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendPayload(payload);
            }
        }, 0, 1000);

        while (true) {
            // do nothing, just wait for the timer to send the payload every second
        }
    }

    private static void sendPayload(byte[] payload) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetSocketAddress dest_address = new InetSocketAddress(IPADDRESS, PORT);
            var packet = new DatagramPacket(payload, payload.length, dest_address);
            socket.send(packet);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}