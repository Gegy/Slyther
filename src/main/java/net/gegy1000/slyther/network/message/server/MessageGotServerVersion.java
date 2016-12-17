package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.ClientNetworkManager;
import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.network.message.client.MessageClientRiddleAnswer;
import net.gegy1000.slyther.network.message.client.MessageClientSetup;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageGotServerVersion extends SlytherServerMessageBase {
    private String version;

    public MessageGotServerVersion() {
    }

    public MessageGotServerVersion(String version) {
        this.version = version;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        buffer.writeASCIIBytes(version);
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client, ClientNetworkManager networkManager) {
        version = buffer.readASCIIBytes();
        networkManager.send(new MessageClientRiddleAnswer(this.version));
        networkManager.send(new MessageClientSetup(client.configuration.nickname, client.configuration.skin));
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { '6' };
    }
}
