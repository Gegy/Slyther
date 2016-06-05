package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.ClientNetworkManager;
import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.game.entity.ClientFood;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.entity.Food;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageNewFood extends SlytherServerMessageBase {
    private Food food;

    public MessageNewFood() {
    }

    public MessageNewFood(Food food) {
        this.food = food;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        buffer.writeUInt8(food.color.ordinal());
        buffer.writeUInt16((int) food.posX + server.configuration.gameRadius);
        buffer.writeUInt16((int) food.posY + server.configuration.gameRadius);
        buffer.writeUInt8((int) (food.size * 5.0F));
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client, ClientNetworkManager networkManager) {
        if (buffer.hasRemaining(5)) {
            Color color = Color.values()[buffer.readUInt8()];
            int x = buffer.readUInt16();
            int y = buffer.readUInt16();
            float size = buffer.readUInt8() / 5.0F;
            client.addEntity(new ClientFood(client, x, y, size, messageId == 'b', color));
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
