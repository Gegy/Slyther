package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.ClientNetworkManager;
import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.game.entity.ClientSnake;
import net.gegy1000.slyther.game.entity.Snake;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

//10 per second
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
        if (turnDirection) {
            buffer.writeUInt8((int) (snake.angle / (2.0F * Math.PI / 256.0F)));
            buffer.writeUInt8((int) (snake.wantedAngle / (2.0F * Math.PI / 256.0F)));
            buffer.writeUInt8((int) (snake.speed * 18.0F));
        } else {
            buffer.writeUInt8((int) (snake.angle / (2.0F * Math.PI / 256.0F)));
            buffer.writeUInt8((int) (snake.speed * 18.0F));
        }
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client, ClientNetworkManager networkManager) {
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
        ClientSnake snake = client.getSnake(id);
        if (snake != null) {
            if (turnDirection != -1) {
                snake.turnDirection = turnDirection;
            }
            if (angle != -1) {
                float foodAngle = (float) ((angle - snake.angle) % SlytherClient.PI_2);
                if (foodAngle < 0) {
                    foodAngle += SlytherClient.PI_2;
                }
                if (foodAngle > Math.PI) {
                    foodAngle -= SlytherClient.PI_2;
                }
                int index = snake.foodAngleIndex;
                for (int i = 0; i < SlytherClient.AFC; i++) {
                    snake.foodAngles[index] = foodAngle * SlytherClient.AFAS[i];
                    index++;
                    if (index >= SlytherClient.AFC) {
                        index = 0;
                    }
                }
                snake.foodAnglesToGo = SlytherClient.AFC;
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
                snake.speedTurnMultiplier = speed / client.SPANG_DV;
                if (snake.speedTurnMultiplier > 1.0F) {
                    snake.speedTurnMultiplier = 1.0F;
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
        if (turnDirection) {
            if (snake.turnDirection != 1) {
                return !(angle || wantedAngle) ? '5' : '4';
            }
        }
        return 'e';
    }
}
