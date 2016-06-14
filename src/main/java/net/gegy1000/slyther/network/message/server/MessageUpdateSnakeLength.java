package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.ClientNetworkManager;
import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.entity.Snake;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageUpdateSnakeLength extends SlytherServerMessageBase {
    private Snake<?> snake;

    public MessageUpdateSnakeLength() {
    }

    public MessageUpdateSnakeLength(Snake<?> snake) {
        this.snake = snake;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        buffer.writeUInt16(snake.id);
        buffer.writeUInt24((int) (snake.fam * 0xFFFFFF));
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client, ClientNetworkManager networkManager) {
        int id = buffer.readUInt16();
        Snake snake = client.getSnake(id);
        if (snake != null) {
            snake.fam = (double) buffer.readUInt24() / 0xFFFFFF;
            snake.updateLength();
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'h' };
    }
}
