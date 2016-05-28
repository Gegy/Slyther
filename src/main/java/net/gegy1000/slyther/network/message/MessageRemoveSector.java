package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.game.Food;
import net.gegy1000.slyther.client.game.Sector;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageRemoveSector extends SlytherServerMessageBase {
    private net.gegy1000.slyther.server.game.Sector sector;

    public MessageRemoveSector() {
    }

    public MessageRemoveSector(net.gegy1000.slyther.server.game.Sector sector) {
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
        for (int i = 0; i < client.foods.size(); i++) {
            Food food = client.foods.get(i);
            if (food.sectorX == x && food.sectorY == y) {
                client.foods.remove(i);
            }
        }
        for (int i = 0; i < client.sectors.size(); i++) {
            Sector sector = client.sectors.get(i);
            if (sector.x == x && sector.y == y) {
                client.sectors.remove(i);
            }
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'w' };
    }
}
