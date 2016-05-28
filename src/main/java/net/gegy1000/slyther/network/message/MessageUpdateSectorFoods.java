package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.client.game.Food;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageUpdateSectorFoods extends SlytherServerMessageBase {
    private net.gegy1000.slyther.server.game.Sector sector;

    public MessageUpdateSectorFoods() {
    }

    public MessageUpdateSectorFoods(net.gegy1000.slyther.server.game.Sector sector) {
        this.sector = sector;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        int gameRadius = server.configuration.gameRadius;
        for (net.gegy1000.slyther.server.game.Food food : sector.foods) {
            buffer.writeUInt8(food.color.ordinal());
            buffer.writeUInt16((int) food.posX + gameRadius);
            buffer.writeUInt16((int) food.posY + gameRadius);
            buffer.writeUInt8(food.size);
        }
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        boolean expectingSectorPosition = false;
        int sectorX = 0;
        int sectorY = 0;
        while (buffer.hasRemaining()) {
            Color color = Color.values()[buffer.readUInt8() % Color.values().length];
            int x = buffer.readUInt16();
            int y = buffer.readUInt16();
            float size = buffer.readUInt8() / 5.0F;
            int id = y * client.GAME_RADIUS * 3 + x;
            Food food = new Food(client, id, x, y, size, true, color);
            if (!expectingSectorPosition) {
                expectingSectorPosition = true;
                sectorX = (int) Math.floor(x / client.SECTOR_SIZE);
                sectorY = (int) Math.floor(y / client.SECTOR_SIZE);
            }
            food.sectorX = sectorX;
            food.sectorY = sectorY;
            if (!client.foods.contains(food)) {
                client.foods.add(food);
            }
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'F' };
    }
}
