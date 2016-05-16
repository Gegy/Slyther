package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
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
        if (client.PROTOCOL_VERSION >= 5) {
            int sendAngle = (int) Math.floor(251 * ang / SlytherClient.PI_2);
            client.lastSendAngle = sendAngle;
            buffer.writeByte((byte) (sendAngle & 0xFF));
            client.lastSendAngleTime = System.currentTimeMillis();
        } else {
            int sendAngle = (int) Math.floor(0xFFFFFF * ang / SlytherClient.PI_2);
            client.lastSendAngle = sendAngle;
            buffer.writeByte((byte) 101);
            buffer.writeByte((byte) (sendAngle >> 16 & 0xFF));
            buffer.writeByte((byte) (sendAngle >> 8 & 0xFF));
            buffer.writeByte((byte) (sendAngle & 0xFF));
            client.lastSendAngleTime = System.currentTimeMillis();
        }
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherServer server) {
    }
}
