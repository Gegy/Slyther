package net.gegy1000.slyther.network.message.client;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherClientMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageSetAngle extends SlytherClientMessageBase {
    public float angle;

    public MessageSetAngle() {
    }

    public MessageSetAngle(float angle) {
        this.angle = angle;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherClient client) {
        int sendAngle = (int) Math.floor((251 * angle) / SlytherClient.PI_2);
        buffer.writeUInt8(sendAngle & 0xFF);
        client.lastSendAngleTime = System.currentTimeMillis();
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        client.snake.wantedAngle = (float) ((buffer.readUInt8() / 251.0F) * SlytherClient.PI_2);
    }
}
