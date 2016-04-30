package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessagePing extends SlytherClientMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherClient client) {
        buffer.writeByte((byte) 112);
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherServer server) {
    }
}