package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.game.Snake;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageUpdateFam extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        int id = buffer.readUInt16();
        Snake snake = client.getSnake(id);
        if (snake != null) {
            snake.fam = (double) buffer.readUInt24() / 0xFFFFFF;
            snake.snl();
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'h' };
    }
}
