package net.gegy1000.slyther.server;

import net.gegy1000.slyther.game.Skin;
import org.java_websocket.WebSocket;

public class ConnectedClient {
    public String name;
    public Skin skin;
    public WebSocket socket;
    public long lastPacketTime;

    public ConnectedClient(WebSocket socket) {
        this.socket = socket;
    }
}
