package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Snake;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageUpdateSnakePosition extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        int id = buffer.readShort();
        int dir = -1;
        float ang = -1;
        float wang = -1;
        float sp = -1;
        if (buffer.hasNext(3)) {
            dir = this.messageId == 'e' ? 1 : 2;
            ang = (float) (2.0F * Math.PI / 256.0F);
            wang = (float) (2.0F * Math.PI / 256.0F);
            sp = buffer.readByte() / 18.0F;
        } else if (buffer.hasNext(2)) {
            if (this.messageId == 'e') {
                ang = (float) (2.0F * Math.PI / 256.0F);
                sp = buffer.readByte() / 18.0F;
            } else if (this.messageId == 'E') {
                dir = 1;
                wang = (float) (2.0F * Math.PI / 256.0F);
                sp = buffer.readByte() / 18.0F;
            } else if (this.messageId == '4') {
                dir = 2;
                wang = (float) (2.0F * Math.PI / 256.0F);
                sp = buffer.readByte() / 18.0F;
            } else if (this.messageId == '5') {
                dir = 2;
                ang = (float) (2.0F * Math.PI / 256.0F);
                wang = (float) (2.0F * Math.PI / 256.0F);
            }
        } else if (buffer.hasNext()) {
            if (this.messageId == 'e') {
                ang = (float) (2.0F * Math.PI / 256.0F);
            } else if (this.messageId == 'E') {
                dir = 1;
                wang = (float) (2.0F * Math.PI / 256.0F);
            } else if (this.messageId == '3') {
                sp = buffer.readByte() / 18.0F;
            }
        }
        Snake snake = client.getSnake(id);
        if (snake != null) {
            if (dir != -1) {
                snake.dir = dir;
            }
            if (ang != -1) {
                float fa = (float) ((ang - snake.ang) % SlytherClient.PI_2);
                if (fa < 0) {
                    fa += SlytherClient.PI_2;
                }
                if (fa > Math.PI) {
                    fa -= SlytherClient.PI_2;
                }
                int fapos = snake.fapos;
                for (int i = 0; i < SlytherClient.AFC; i++) {
                    snake.fas[fapos] = fa * SlytherClient.AFAS[i];
                    fapos++;
                    if (fapos >= SlytherClient.AFC) {
                        fapos = 0;
                    }
                }
                snake.fatg = SlytherClient.AFC;
                snake.ang = ang;
            }
            if (wang != -1) {
                snake.wang = wang;
                if (snake != client.player) {
                    snake.eang = wang;
                }
            }
            if (sp != -1) {
                snake.sp = sp;
                snake.spang = sp / client.SPANG_DIV;
                if (snake.spang > 1.0F) {
                    snake.spang = 1.0F;
                }
            }
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'e', 'E', '3', '4', '5' };
    }
}
