package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageClientPing extends SlytherClientMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherClient client) {
        buffer.writeUInt8(251);
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        client.send(server, new MessagePing());
    }
}