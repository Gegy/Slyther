package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Food;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageRemoveFood extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        int x = buffer.readShort();
        int y = buffer.readShort();
        int id = y * client.GAME_RADIUS + x;
        Food food = client.getFood(id);
        if (food != null) {
            food.eaten = true;
            if (buffer.hasNext(2)) {
                food.eater = client.getSnake(buffer.readShort());
                food.eatenFr = 0;
            } else {
                client.foods.remove(food);
            }
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'c' };
    }
}
