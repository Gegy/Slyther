package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.ProfanityHandler;
import net.gegy1000.slyther.game.Skin;
import net.gegy1000.slyther.client.game.Snake;
import net.gegy1000.slyther.game.SnakePoint;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

import java.util.ArrayList;
import java.util.List;

public class MessageNewSnake extends SlytherServerMessageBase {
    private boolean removing;
    private boolean dead;
    private net.gegy1000.slyther.server.game.Snake snake;

    public MessageNewSnake() {
    }

    public MessageNewSnake(net.gegy1000.slyther.server.game.Snake snake, boolean dead) {
        this(snake);
        this.dead = dead;
        removing = true;
    }

    public MessageNewSnake(net.gegy1000.slyther.server.game.Snake snake) {
        this.snake = snake;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        buffer.writeUInt16(snake.id);
        if (removing) {
            buffer.writeUInt8(dead ? 1 : 0);
        } else {
            buffer.writeUInt24( (int) (snake.ang / ((2.0F * Math.PI) / 0xFFFFFF)));
            buffer.writeUInt8(0);
            buffer.writeUInt24( (int) (snake.wang / ((2.0F * Math.PI) / 0xFFFFFF)));
            buffer.writeUInt16((int) (snake.sp * 1000.0F));
            buffer.writeUInt24((int) (snake.fam * 0xFFFFFF));
            buffer.writeUInt8(snake.client.skin.ordinal());
            int gameRadius = server.configuration.gameRadius;
            buffer.writeUInt24((int) ((snake.posX + gameRadius) * 5.0F));
            buffer.writeUInt24((int) ((snake.posY + gameRadius) * 5.0F));
            String name = snake.client.name;
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
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        int id = buffer.readUInt16();
        if (buffer.hasRemaining(4)) {
            float angle = (float) (buffer.readUInt24() * (2 * Math.PI / 0xFFFFFF));
            buffer.skipBytes(1);
            float wang = (float) (buffer.readUInt24() * (2 * Math.PI / 0xFFFFFF));
            float sp = buffer.readUInt16() / 1000.0F;
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
                SnakePoint point = new SnakePoint(pointX, pointY);
                point.ebx = pointX - prevPointX;
                point.eby = pointY - prevPointY;
                points.add(point);
            }
            Snake snake = new Snake(client, id, x, y, skin, angle, points);
            if (client.player == null) {
                client.viewX = pointX;
                client.viewY = pointY;
                client.player = snake;
                snake.md = false;
                snake.prevMd = false;
                snake.name = client.configuration.nickname;
            } else {
                snake.name = name;
                if (!ProfanityHandler.isClean(name)) {
                    snake.name = "";
                }
            }
            snake.eang = snake.wang = wang;
            snake.sp = sp;
            snake.spang = sp / client.SPANG_DIV;
            if (snake.spang > 1.0F) {
                snake.spang = 1.0F;
            }
            snake.fam = fam;
            snake.sc = Math.min(6.0F, 1.0F + (snake.sct - 2.0F) / 106.0F);
            snake.scang = (float) (0.13F + 0.87F * Math.pow((7.0F - snake.sc) / 6.0F, 2.0F));
            snake.ssp = client.NSP1 + client.NSP2 * snake.sc;
            snake.fsp = snake.ssp + 0.1F;
            snake.wsep = snake.sc * 6.0F;
            float max = SlytherClient.NSEP / client.gsc;
            if (snake.wsep < max) {
                snake.wsep = max;
            }
            snake.sep = snake.wsep;

            System.out.println("Added snake \"" + snake.name + "\" with skin " + snake.rcv);
            client.snakes.add(snake);

            snake.snl();
        } else {
            boolean dead = buffer.readUInt8() == 1;
            Snake snake = client.getSnake(id);
            if (snake != null) {
                snake.id = -1234;
                if (dead) {
                    snake.dead = true;
                    snake.deadAmt = 0;
                    snake.edir = 0;
                } else {
                    client.snakes.remove(snake);
                }
            }
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 's' };
    }
}
