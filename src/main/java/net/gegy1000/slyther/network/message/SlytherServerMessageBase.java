package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

/**
 * Message being sent to the client
 */
public abstract class SlytherServerMessageBase {
    public byte messageType;
    public short timeSinceLastMessage;

    public final void writeBase(MessageByteBuffer buffer, SlytherServer server) {
        buffer.writeShort((short) 0); //TODO timeSinceLastMessage
        buffer.writeByte((byte) this.getMessageId());
        this.write(buffer, server);
    }

    public final void readBase(MessageByteBuffer buffer, SlytherClient client)  {
        this.read(buffer, client);
    }

    public abstract void write(MessageByteBuffer buffer, SlytherServer server);

    public abstract void read(MessageByteBuffer buffer, SlytherClient client);

    public abstract int getMessageId();
}
