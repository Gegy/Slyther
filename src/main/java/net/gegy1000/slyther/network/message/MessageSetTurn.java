package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageSetTurn extends SlytherClientMessageBase {
    public byte direction;

    public MessageSetTurn() {
    }

    public MessageSetTurn(byte direction) {
        this.direction = direction;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherClient client) {
        if (client.PROTOCOL_VERSION >= 5) {
            buffer.writeByte((byte) 252);
        } else {
            buffer.writeByte((byte) 108);
        }
        buffer.writeByte(direction);
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherServer server) {
    }
}
