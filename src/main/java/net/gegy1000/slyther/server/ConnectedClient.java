package net.gegy1000.slyther.server;

import net.gegy1000.slyther.game.Skin;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import org.java_websocket.WebSocket;

public class ConnectedClient {
    public String name;
    public Skin skin;
    public WebSocket socket;
    public long lastPacketTime;

    public ConnectedClient(WebSocket socket) {
        this.socket = socket;
    }

    public void send(SlytherServer server, SlytherServerMessageBase message) {
        try {
            MessageByteBuffer buffer = new MessageByteBuffer();
            buffer.writeUInt16((int) (System.currentTimeMillis() - lastPacketTime));
            buffer.writeUInt8(message.getMessageIds()[0]); //TODO Select
            message.write(buffer, server);
            socket.send(buffer.bytes());
        } catch (Exception e) {
            System.err.println("An error occurred while sending message " + message.getClass().getName());
            e.printStackTrace();
        }
    }
}
