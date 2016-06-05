package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.ClientNetworkManager;
import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.entity.Sector;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageRemoveSector extends SlytherServerMessageBase {
    private Sector sector;

    public MessageRemoveSector() {
    }

    public MessageRemoveSector(Sector sector) {
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
        int x = buffer.readUInt8();
        int y = buffer.readUInt8();
        Sector remove = null;
        for (Sector sector : client.getSectors()) {
            if (sector.posX == x && sector.posY == y) {
                remove = sector;
                break;
            }
        }
        client.removeSector(remove);
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'w' };
    }
}
