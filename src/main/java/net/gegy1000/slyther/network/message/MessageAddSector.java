package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.game.Sector;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageAddSector extends SlytherServerMessageBase {
    private net.gegy1000.slyther.server.game.Sector sector;

    public MessageAddSector() {
    }

    public MessageAddSector(net.gegy1000.slyther.server.game.Sector sector) {
        this.sector = sector;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        int offset = server.configuration.gameRadius / server.configuration.sectorSize;
        buffer.writeUInt8((int) sector.posX + offset);
        buffer.writeUInt8((int) sector.posY + offset);
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        int x = buffer.readUInt8();
        int y = buffer.readUInt8();
        client.sectors.add(new Sector(x, y));
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'W' };
    }
}
