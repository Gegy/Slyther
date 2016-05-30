package net.gegy1000.slyther.network.message.client;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherClientMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageSetAngle extends SlytherClientMessageBase {
    public float ang;

    public MessageSetAngle() {
    }

    public MessageSetAngle(float ang) {
        this.ang = ang;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherClient client) {
        int sendAngle = (int) Math.floor((251 * ang) / SlytherClient.PI_2);
        client.lastSendAngle = sendAngle;
        buffer.writeUInt8(sendAngle & 0xFF);
        client.lastSendAngleTime = System.currentTimeMillis();
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        client.snake.wantedAngle = (float) ((buffer.readUInt8() / SlytherClient.PI_2) * 251);
    }
}
