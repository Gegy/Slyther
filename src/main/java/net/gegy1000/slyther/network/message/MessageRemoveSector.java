package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Food;
import net.gegy1000.slyther.game.Sector;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageRemoveSector extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        int sx = buffer.readByte();
        int sy = buffer.readByte();
        for (int i = 0; i < client.foods.size(); i++) {
            Food food = client.foods.get(i);
            if (food.sx == sx && food.sy == sy) {
                client.foods.remove(i);
            }
        }
        for (int i = 0; i < client.sectors.size(); i++) {
            Sector sector = client.sectors.get(i);
            if (sector.x == sx && sector.y == sy) {
                client.sectors.remove(i);
            }
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'w' };
    }
}
