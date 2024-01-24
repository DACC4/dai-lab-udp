package ch.heigvd;

import java.util.Map;
import java.util.UUID;

public class MusicianMessage {
    private String uuid;
    private String sound;

    final static Map<String, String> sounds = Map.of(
            "piano", "ti-ta-ti",
            "trumpet", "pouet",
            "flute", "trulu",
            "violin", "gzi-gzi",
            "drum", "boum-boum"
    );

    public MusicianMessage(String instrument) {
        sound = sounds.get(instrument);
        if (sound == null) {
            throw new IllegalArgumentException("Unknown instrument");
        }
        uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }

    public String getSound() {
        return sound;
    }
}
