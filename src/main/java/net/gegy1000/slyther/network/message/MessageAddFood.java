package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Food;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageAddFood extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        byte color = buffer.readByte();
        short x = buffer.readShort();
        short y = buffer.readShort();
        byte size = (byte) (buffer.readByte() / 5);
        client.addFood(new Food(y * client.GAME_RADIUS * 3 + x, x, y, size, color));
    }

    @Override
    public int getMessageId() {
        return 'F';
    }
}
