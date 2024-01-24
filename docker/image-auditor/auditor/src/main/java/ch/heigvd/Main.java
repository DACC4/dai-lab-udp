package ch.heigvd;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;

class Main {
    final static String IPADDRESS = "239.255.22.5";
    final static int PORT = 9904;
    final static Map<String, String> sounds = Map.of(
            "ti-ta-ti", "piano",
            "pouet", "trumpet",
            "trulu", "flute",
            "gzi-gzi", "violin",
            "boum-boum", "drum"
    );
    static Auditor auditor;
    static ExecutorService threadPool;

    public static void main(String[] args) {
        // Create a virtual thread pool
        threadPool = Executors.newVirtualThreadPerTaskExecutor();

        // Create the auditor and run it in a separate virtual thread
        auditor = new Auditor();
        threadPool.submit(() -> auditor.run());

        // Start receiving messages
        startReceiving();
    }

    private static void startReceiving() {
        try (MulticastSocket socket = new MulticastSocket(PORT)) {
            InetSocketAddress group_address = new InetSocketAddress(IPADDRESS, PORT);
            NetworkInterface netif = NetworkInterface.getByName("eth0");
            socket.joinGroup(group_address, netif);

            // Receive messages
            while (true) {
                receiveMessage(socket, group_address, netif);
            }

            //socket.leaveGroup(group_address, netif);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void receiveMessage(MulticastSocket socket, InetSocketAddress groupAddress, NetworkInterface netif) throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String message = new String(packet.getData(), 0, packet.getLength(), UTF_8);

        // Payload is a JSON string, so we can use Gson to parse it ({"uuid": "67ada557-547e-41f0-a7c1-ea743c756a41", "sound": "pouet"})
        JsonObject payload = new JsonParser().parse(message).getAsJsonObject();
        String uuid = payload.get("uuid").getAsString();
        String sound = payload.get("sound").getAsString();

        auditor.updateMusician(uuid, sounds.get(sound));
    }
}