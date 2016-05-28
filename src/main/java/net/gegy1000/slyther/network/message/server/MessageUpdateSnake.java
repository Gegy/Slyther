package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.entity.Snake;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageUpdateSnake extends SlytherServerMessageBase {
    private Snake snake;
    private boolean dir;
    private boolean ang;
    private boolean wang;
    private boolean sp;

    public MessageUpdateSnake() {
    }

    public MessageUpdateSnake(Snake snake, boolean dir, boolean ang, boolean wang, boolean sp) {
        this.snake = snake;
        this.dir = dir;
        this.ang = ang;
        this.wang = wang;
        this.sp = sp;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        buffer.writeUInt16(snake.id);
        if (dir && ang && wang && sp) {
            buffer.writeUInt8((int) (snake.angle / (2.0F * Math.PI / 256.0F)));
            buffer.writeUInt8((int) (snake.wang / (2.0F * Math.PI / 256.0F)));
            buffer.writeUInt8((int) (snake.speed * 18.0F));
        } else if (ang && sp) {
            buffer.writeUInt8((int) (snake.angle / (2.0F * Math.PI / 256.0F)));
            buffer.writeUInt8((int) (snake.speed * 18.0F));
        } else if ((dir) && (snake.turnDirection == 1 || snake.turnDirection == 2) && wang && sp) {
            buffer.writeUInt8((int) (snake.wang / (2.0F * Math.PI / 256.0F)));
            buffer.writeUInt8((int) (snake.speed * 18.0F));
        } else if ((dir && snake.turnDirection == 2) && ang && wang) {
            buffer.writeUInt8((int) (snake.angle / (2.0F * Math.PI / 256.0F)));
            buffer.writeUInt8((int) (snake.wang / (2.0F * Math.PI / 256.0F)));
        } else if (ang) {
            buffer.writeUInt8((int) (snake.angle / (2.0F * Math.PI / 256.0F)));
        } else if ((dir && snake.turnDirection == 1) && wang) {
            buffer.writeUInt8((int) (snake.wang / (2.0F * Math.PI / 256.0F)));
        } else if (sp) {
            buffer.writeUInt8((int) (snake.speed * 18.0F));
        }
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        int id = buffer.readUInt16();
        int dir = -1;
        float ang = -1;
        float wang = -1;
        float sp = -1;
        if (buffer.hasRemaining(3)) {
            dir = messageId == 'e' ? 1 : 2;
            ang = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
            wang = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
            sp = buffer.readUInt8() / 18.0F;
        } else if (buffer.hasRemaining(2)) {
            if (messageId == 'e') {
                ang = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
                sp = buffer.readUInt8() / 18.0F;
            } else if (messageId == 'E') {
                dir = 1;
                wang = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
                sp = buffer.readUInt8() / 18.0F;
            } else if (messageId == '4') {
                dir = 2;
                wang = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
                sp = buffer.readUInt8() / 18.0F;
            } else if (messageId == '5') {
                dir = 2;
                ang = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
                wang = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
            }
        } else if (buffer.hasRemaining()) {
            if (messageId == 'e') {
                ang = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
            } else if (messageId == 'E') {
                dir = 1;
                wang = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
            } else if (messageId == '3') {
                sp = buffer.readUInt8() / 18.0F;
            }
        }
        Snake snake = client.getSnake(id);
        if (snake != null) {
            if (dir != -1) {
                snake.turnDirection = dir;
            }
            if (ang != -1) {
                float fa = (float) ((ang - snake.angle) % SlytherClient.PI_2);
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
                snake.angle = ang;
            }
            if (wang != -1) {
                snake.wang = wang;
                if (snake != client.player) {
                    snake.eang = wang;
                }
            }
            if (sp != -1) {
                snake.speed = sp;
                snake.spang = sp / client.SPANG_DV;
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

    @Override
    public int getSendMessageId() {
        if (dir && ang && wang && sp) {
            return snake.turnDirection == 1 ? 'e' : 'E';
        } else if (ang && sp) {
            return 'e';
        } else if ((dir && snake.turnDirection == 1) && wang && sp) {
            return 'E';
        } else if ((dir && snake.turnDirection == 2) && wang && sp) {
            return '4';
        } else if ((dir && snake.turnDirection == 2) && ang && wang) {
            return '5';
        } else if (ang) {
            return 'e';
        } else if ((dir && snake.turnDirection == 1) && wang) {
            return 'E';
        } else if (sp) {
            return '3';
        }
        return 'e';
    }
}
