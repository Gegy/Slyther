package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.ClientNetworkManager;
import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.game.entity.ClientSector;
import net.gegy1000.slyther.game.entity.Sector;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageAddSector extends SlytherServerMessageBase {
    private Sector<?> sector;

    public MessageAddSector() {
    }

    public MessageAddSector(Sector<?> sector) {
        this.sector = sector;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        int offset = server.configuration.gameRadius / server.configuration.sectorSize;
        buffer.writeUInt8(sector.posX + offset);
        buffer.writeUInt8(sector.posY + offset);
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client, ClientNetworkManager networkManager) {
        client.addSector(new ClientSector(client, buffer.readUInt8(), buffer.readUInt8()));
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'W' };
    }
}
