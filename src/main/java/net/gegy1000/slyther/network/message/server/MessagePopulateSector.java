package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.entity.Food;
import net.gegy1000.slyther.game.entity.Sector;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessagePopulateSector extends SlytherServerMessageBase {
    private Sector sector;

    public MessagePopulateSector() {
    }

    public MessagePopulateSector(Sector sector) {
        this.sector = sector;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        int gameRadius = server.configuration.gameRadius;
        for (Food food : sector.foods) {
            buffer.writeUInt8(food.color.ordinal());
            buffer.writeUInt16((int) food.posX + gameRadius);
            buffer.writeUInt16((int) food.posY + gameRadius);
            buffer.writeUInt8((int) food.size * 5);
        }
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        while (buffer.hasRemaining()) {
            Color color = Color.values()[buffer.readUInt8() % Color.values().length];
            int x = buffer.readUInt16();
            int y = buffer.readUInt16();
            float size = buffer.readUInt8() / 5.0F;
            client.addEntity(new Food(client, x, y, size, true, color));
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'F' };
    }
}
