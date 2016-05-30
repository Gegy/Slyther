package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

/**
 * Message being sent from the client
 */
public abstract class SlytherClientMessageBase implements Message {
    public abstract void write(MessageByteBuffer buffer, SlytherClient client);

    public abstract void read(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client);
}
