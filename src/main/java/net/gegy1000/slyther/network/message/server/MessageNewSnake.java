package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.ClientNetworkManager;
import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.game.entity.ClientSnake;
import net.gegy1000.slyther.game.ProfanityHandler;
import net.gegy1000.slyther.game.Skin;
import net.gegy1000.slyther.game.entity.Snake;
import net.gegy1000.slyther.game.entity.SnakePoint;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;
import net.gegy1000.slyther.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MessageNewSnake extends SlytherServerMessageBase {
    private boolean removing;
    private boolean dead;
    private Snake<?> snake;

    public MessageNewSnake() {
    }

    public MessageNewSnake(Snake snake, boolean dead) {
        this(snake);
        this.dead = dead;
        removing = true;
    }

    public MessageNewSnake(Snake snake) {
        this.snake = snake;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        buffer.writeUInt16(snake.id);
        if (removing) {
            buffer.writeUInt8(dead ? 1 : 0);
        } else {
            buffer.writeUInt24( (int) (snake.angle / ((2.0F * Math.PI) / 0xFFFFFF)));
            buffer.writeUInt8(0);
            buffer.writeUInt24( (int) (snake.wantedAngle / ((2.0F * Math.PI) / 0xFFFFFF)));
            buffer.writeUInt16((int) (snake.speed * 1000.0F));
            buffer.writeUInt24((int) (snake.fam * 0xFFFFFF));
            buffer.writeUInt8(snake.skin.ordinal());
            int gameRadius = server.configuration.gameRadius;
            buffer.writeUInt24((int) ((snake.posX + gameRadius) * 5.0F));
            buffer.writeUInt24((int) ((snake.posY + gameRadius) * 5.0F));
            String name = snake.name;
            buffer.writeUInt8(name.length());
            for (int i = 0; i < name.length(); i++) {
                buffer.writeUInt8((byte) name.charAt(i));
            }
            boolean head = true;
            float prevPosX = 0.0F;
            float prevPosY = 0.0F;
            for (SnakePoint point : snake.points) {
                float posX = point.posX + gameRadius;
                float posY = point.posY + gameRadius;
                if (head) {
                    buffer.writeUInt24((int) (posX * 5.0F));
                    buffer.writeUInt24((int) (posY * 5.0F));
                    head = false;
                } else {
                    buffer.writeUInt8((int) ((posX - prevPosX + 127) / 2.0F));
                    buffer.writeUInt8((int) ((posY - prevPosY + 127) / 2.0F));
                }
                prevPosX = posX;
                prevPosY = posY;
            }
        }
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client, ClientNetworkManager networkManager) {
        int id = buffer.readUInt16();
        if (buffer.hasRemaining(4)) {
            float angle = (float) (buffer.readUInt24() * (2 * Math.PI / 0xFFFFFF));
            buffer.skipBytes(1);
            float wantedAngle = (float) (buffer.readUInt24() * (2 * Math.PI / 0xFFFFFF));
            float speed = buffer.readUInt16() / 1000.0F;
            double fam = (double) buffer.readUInt24() / 0xFFFFFF;
            int ski = buffer.readUInt8();
            Skin skin = Skin.values()[ski < Skin.values().length ? ski : 0];
            float x = buffer.readUInt24() / 5.0F;
            float y = buffer.readUInt24() / 5.0F;
            String name = "";
            int nameLength = buffer.readUInt8();
            for (int i = 0; i < nameLength; i++) {
                name += (char) buffer.readUInt8();
            }
            float prevPointX;
            float prevPointY;
            float pointX = 0;
            float pointY = 0;
            List<SnakePoint> points = new ArrayList<>();
            while (buffer.hasRemaining(2)) {
                prevPointX = pointX;
                prevPointY = pointY;
                if (points.isEmpty()) {
                    pointX = buffer.readUInt24() / 5.0F;
                    pointY = buffer.readUInt24() / 5.0F;
                    prevPointX = pointX;
                    prevPointY = pointY;
                } else {
                    pointX += (buffer.readUInt8() - 127) / 2.0F;
                    pointY += (buffer.readUInt8() - 127) / 2.0F;
                }
                SnakePoint point = new SnakePoint(client, pointX, pointY);
                point.deltaX = pointX - prevPointX;
                point.deltaY = pointY - prevPointY;
                points.add(point);
            }
            if (client.player == null) {
                name = client.configuration.nickname;
            } else {
                if (!ProfanityHandler.isClean(name)) {
                    name = "";
                }
            }
            ClientSnake snake = new ClientSnake(client, name, id, x, y, skin, angle, points);
            if (client.player == null) {
                client.viewX = pointX;
                client.viewY = pointY;
                client.player = snake;
                snake.accelerating = false;
                snake.wasAccelerating = false;
            }
            snake.eyeAngle = snake.wantedAngle = wantedAngle;
            snake.speed = speed;
            snake.speedTurnMultiplier = speed / client.SPANG_DV;
            if (snake.speedTurnMultiplier > 1.0F) {
                snake.speedTurnMultiplier = 1.0F;
            }
            snake.fam = fam;
            snake.scale = Math.min(6.0F, 1.0F + (snake.sct - 2.0F) / 106.0F);
            snake.scaleTurnMultiplier = (float) (0.13F + 0.87F * Math.pow((7.0F - snake.scale) / 6.0F, 2.0F));
            snake.wantedSeperation = snake.scale * 6.0F;
            float max = SlytherClient.NSEP / client.globalScale;
            if (snake.wantedSeperation < max) {
                snake.wantedSeperation = max;
            }
            snake.partSeparation = snake.wantedSeperation;

            Log.debug("Added snake \"{}\" with skin {}", snake.name, snake.skin);
            client.addEntity(snake);

            snake.updateLength();
        } else {
            boolean dead = buffer.readUInt8() == 1;
            Snake snake = client.getSnake(id);
            if (snake != null) {
                Log.debug("Remove snake \"{}\" with skin {}, dead {}", snake.name, snake.skin, dead);
                if (dead) {
                    snake.dead = true;
                    snake.deadAmt = 0;
                    snake.edir = 0;
                } else {
                    client.removeEntity(snake);
                }
            }
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 's' };
    }
}
