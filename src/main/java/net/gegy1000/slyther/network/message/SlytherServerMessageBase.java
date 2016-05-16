package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

/**
 * Message being sent to the client
 */
public abstract class SlytherServerMessageBase {
    public byte messageId;
    public short serverTimeDelta;

    public final void writeBase(MessageByteBuffer buffer, SlytherServer server) {
//        buffer.writeShort((short) 0); //TODO serverTimeDelta
//        buffer.writeByte((byte) this.getMessageIds());
        this.write(buffer, server);
    }

    public final void readBase(MessageByteBuffer buffer, SlytherClient client)  {
        this.read(buffer, client);
    }

    public abstract void write(MessageByteBuffer buffer, SlytherServer server);

    public abstract void read(MessageByteBuffer buffer, SlytherClient client);

    public abstract int[] getMessageIds();
}
