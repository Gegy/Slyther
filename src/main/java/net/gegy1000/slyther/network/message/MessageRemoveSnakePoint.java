package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Snake;
import net.gegy1000.slyther.game.SnakePoint;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageRemoveSnakePoint extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        Snake snake = client.getSnake(buffer.readUInt16());
        if (snake != null) {
            if (buffer.hasRemaining(3)) {
                snake.fam = (double) buffer.readUInt24() / 0xFFFFFF;
            }
            for (SnakePoint point : snake.pts) {
                if (!point.dying) {
                    point.dying = true;
                    snake.sct--;
                    snake.sc = Math.min(6.0F, (snake.sct - 2.0F) / 106.0F + 1.0F);
                    snake.scang = (float) (Math.pow((7.0F - snake.sc) / 6.0F, 2.0F) * 0.87F + 0.13F);
                    snake.ssp = client.NSP1 + client.NSP2 * snake.sc;
                    snake.fsp = snake.ssp + 0.1F;
                    snake.wsep = snake.sc * 6.0F;
                    float max = SlytherClient.NSEP / client.gsc;
                    if (snake.wsep > max) {
                        snake.wsep = max;
                    }
                    break;
                }
            }
            snake.snl();
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'r' };
    }
}
