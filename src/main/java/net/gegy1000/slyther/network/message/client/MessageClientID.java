package net.gegy1000.slyther.network.message.client;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherClientMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageClientID extends SlytherClientMessageBase {
    private static final String ALPHANUMERIC_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";

    @Override
    public void write(MessageByteBuffer buffer, SlytherClient client) {
        String id = "";
        for (int i = 0; i < 24; i++) {
            id += (char) (65 + (Math.random() < 0.5 ? 0 : 32) + ALPHANUMERIC_CHARACTERS.indexOf(i) + Math.floor(26 * Math.random()));
        }
        buffer.writeASCIIBytes(id);
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
    }
}