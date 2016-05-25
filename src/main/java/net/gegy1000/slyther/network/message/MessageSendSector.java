package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.Food;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageSendSector extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        boolean first = false;
        int sectorX = 0;
        int sectorY = 0;
        while (buffer.hasRemaining()) {
            Color color = Color.values()[buffer.read()];
            int x = buffer.readShort();
            int y = buffer.readShort();
            float size = buffer.read() / 5.0F;
            int id = y * client.GAME_RADIUS * 3 + x;
            Food food = new Food(client, id, x, y, size, true, color);
            if (!first) {
                first = true;
                sectorX = (int) Math.floor(x / client.SECTOR_SIZE);
                sectorY = (int) Math.floor(y / client.SECTOR_SIZE);
            }
            food.sx = sectorX;
            food.sy = sectorY;
            client.foods.add(food);
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'F' };
    }
}
