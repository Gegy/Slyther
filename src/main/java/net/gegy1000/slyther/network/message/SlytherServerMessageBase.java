package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

/**
 * Message being sent to the client
 */
public abstract class SlytherServerMessageBase {
    public byte messageId;
    public int serverTimeDelta;

    public final void writeBase(MessageByteBuffer buffer, SlytherServer server) {
//        buffer.writeShort((short) 0); //TODO serverTimeDelta
//        buffer.writeByte((byte) this.getMessageIds());
        write(buffer, server);
    }

    public final void readBase(MessageByteBuffer buffer, SlytherClient client)  {
        read(buffer, client);
    }

    public abstract void write(MessageByteBuffer buffer, SlytherServer server);

    public abstract void read(MessageByteBuffer buffer, SlytherClient client);

    public abstract int[] getMessageIds();
}
