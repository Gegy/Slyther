package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.ClientNetworkManager;
import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessagePing extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        buffer.writeUInt8('p');
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client, ClientNetworkManager networkManager) {
        networkManager.waitingForPingReturn = false;
        if (client.lagging) {
            client.errorTime *= client.lagMultiplier;
            client.lagging = false;
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'p' };
    }
}