package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.entity.Snake;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageUpdateSnake extends SlytherServerMessageBase {
    private Snake snake;
    private boolean turnDirection;
    private boolean angle;
    private boolean wantedAngle;
    private boolean speed;

    public MessageUpdateSnake() {
    }

    public MessageUpdateSnake(Snake snake, boolean turnDirection, boolean angle, boolean wantedAngle, boolean speed) {
        this.snake = snake;
        this.turnDirection = turnDirection;
        this.angle = angle;
        this.wantedAngle = wantedAngle;
        this.speed = speed;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        buffer.writeUInt16(snake.id);
        if (turnDirection && angle && wantedAngle && speed) {
            buffer.writeUInt8((int) (snake.angle / (2.0F * Math.PI / 256.0F)));
            buffer.writeUInt8((int) (snake.wantedAngle / (2.0F * Math.PI / 256.0F)));
            buffer.writeUInt8((int) (snake.speed * 18.0F));
        } else if (angle && speed) {
            buffer.writeUInt8((int) (snake.angle / (2.0F * Math.PI / 256.0F)));
            buffer.writeUInt8((int) (snake.speed * 18.0F));
        } else if ((turnDirection) && (snake.turnDirection == 1 || snake.turnDirection == 2) && wantedAngle && speed) {
            buffer.writeUInt8((int) (snake.wantedAngle / (2.0F * Math.PI / 256.0F)));
            buffer.writeUInt8((int) (snake.speed * 18.0F));
        } else if ((turnDirection && snake.turnDirection == 2) && angle && wantedAngle) {
            buffer.writeUInt8((int) (snake.angle / (2.0F * Math.PI / 256.0F)));
            buffer.writeUInt8((int) (snake.wantedAngle / (2.0F * Math.PI / 256.0F)));
        } else if (angle) {
            buffer.writeUInt8((int) (snake.angle / (2.0F * Math.PI / 256.0F)));
        } else if ((turnDirection && snake.turnDirection == 1) && wantedAngle) {
            buffer.writeUInt8((int) (snake.wantedAngle / (2.0F * Math.PI / 256.0F)));
        } else if (speed) {
            buffer.writeUInt8((int) (snake.speed * 18.0F));
        }
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        int id = buffer.readUInt16();
        int turnDirection = -1;
        float angle = -1;
        float wantedAngle = -1;
        float speed = -1;
        if (buffer.hasRemaining(3)) {
            turnDirection = messageId == 'e' ? 1 : 2;
            angle = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
            wantedAngle = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
            speed = buffer.readUInt8() / 18.0F;
        } else if (buffer.hasRemaining(2)) {
            if (messageId == 'e') {
                angle = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
                speed = buffer.readUInt8() / 18.0F;
            } else if (messageId == 'E') {
                turnDirection = 1;
                wantedAngle = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
                speed = buffer.readUInt8() / 18.0F;
            } else if (messageId == '4') {
                turnDirection = 2;
                wantedAngle = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
                speed = buffer.readUInt8() / 18.0F;
            } else if (messageId == '5') {
                turnDirection = 2;
                angle = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
                wantedAngle = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
            }
        } else if (buffer.hasRemaining()) {
            if (messageId == 'e') {
                angle = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
            } else if (messageId == 'E') {
                turnDirection = 1;
                wantedAngle = (float) (buffer.readUInt8() * (2.0F * Math.PI / 256.0F));
            } else if (messageId == '3') {
                speed = buffer.readUInt8() / 18.0F;
            }
        }
        Snake snake = client.getSnake(id);
        if (snake != null) {
            if (turnDirection != -1) {
                snake.turnDirection = turnDirection;
            }
            if (angle != -1) {
                float fa = (float) ((angle - snake.angle) % SlytherClient.PI_2);
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
                snake.angle = angle;
            }
            if (wantedAngle != -1) {
                snake.wantedAngle = wantedAngle;
                if (snake != client.player) {
                    snake.eyeAngle = wantedAngle;
                }
            }
            if (speed != -1) {
                snake.speed = speed;
                snake.spang = speed / client.SPANG_DV;
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
        if (turnDirection && angle && wantedAngle && speed) {
            return snake.turnDirection == 1 ? 'e' : 'E';
        } else if (angle && speed) {
            return 'e';
        } else if ((turnDirection && snake.turnDirection == 1) && wantedAngle && speed) {
            return 'E';
        } else if ((turnDirection && snake.turnDirection == 2) && wantedAngle && speed) {
            return '4';
        } else if ((turnDirection && snake.turnDirection == 2) && angle && wantedAngle) {
            return '5';
        } else if (angle) {
            return 'e';
        } else if ((turnDirection && snake.turnDirection == 1) && wantedAngle) {
            return 'E';
        } else if (speed) {
            return '3';
        }
        return 'e';
    }
}
