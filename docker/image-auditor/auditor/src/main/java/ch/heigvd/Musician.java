package ch.heigvd;

import com.google.gson.Gson;

class Musician {
    String uuid;
    String instrument;
    long lastActivity;

    public Musician(String uuid, String instrument, long lastActivity) {
        this.uuid = uuid;
        this.instrument = instrument;
        this.lastActivity = lastActivity;
    }

    public String json() {
        return new Gson().toJson(this);
    }
}

