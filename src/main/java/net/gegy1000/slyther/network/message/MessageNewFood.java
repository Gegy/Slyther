package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.game.Food;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageNewFood extends SlytherServerMessageBase {
    private net.gegy1000.slyther.server.game.Food food;

    public MessageNewFood() {
    }

    public MessageNewFood(net.gegy1000.slyther.server.game.Food food) {
        this.food = food;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        buffer.writeUInt8(food.color.ordinal());
        buffer.writeUInt16((int) food.posX + server.configuration.gameRadius);
        buffer.writeUInt16((int) food.posY + server.configuration.gameRadius);
        buffer.writeUInt8(food.size);
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        if (buffer.hasRemaining(5)) {
            Color color = Color.values()[buffer.readUInt8()];
            int x = buffer.readUInt16();
            int y = buffer.readUInt16();
            int id = y * client.GAME_RADIUS * 3 + x;
            float size = buffer.readUInt8() / 5.0F;
            Food food = new Food(client, id, x, y, size, messageId == 'b', color);
            food.sectorX = (int) Math.floor((float) x / client.SECTOR_SIZE);
            food.sectorY = (int) Math.floor((float) y / client.SECTOR_SIZE);
            if (!client.foods.contains(food)) {
                client.foods.add(food);
            }
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'b', 'f' };
    }

    @Override
    public int getSendMessageId() {
        return food.isNatural ? 'f' : 'b';
    }
}
