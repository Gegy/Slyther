package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Food;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageFoodEaten extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        short x = buffer.readShort();
        short y = buffer.readShort();
        int id = y * client.GAME_RADIUS * 3 + x;
        Food food = client.getFood(id);
        if (food != null) {
            if (buffer.hasNext(2)) {
                short eaterId = buffer.readShort();
                System.out.println("Food eaten by " + eaterId);
            }
            client.removeFood(id);
        }
    }

    @Override
    public int getMessageId() {
        return 'c';
    }
}
