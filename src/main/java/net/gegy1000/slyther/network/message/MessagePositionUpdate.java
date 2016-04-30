package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;
import net.gegy1000.slyther.game.Snake;

public class MessagePositionUpdate extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        Snake snake = client.getSnake(buffer.readShort());
        int x = buffer.readShort(); //TODO int24?
        int y = buffer.readShort();
        if (snake != null) {
            snake.setPosition(x, y);
        }
    }

    @Override
    public int getMessageId() {
        return 'g';
    }
}
