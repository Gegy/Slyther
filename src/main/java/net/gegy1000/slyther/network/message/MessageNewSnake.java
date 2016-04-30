package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.SnakePart;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;
import net.gegy1000.slyther.game.Skin;
import net.gegy1000.slyther.game.Snake;

import java.util.ArrayList;
import java.util.List;

public class MessageNewSnake extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        short snakeId = buffer.readShort();
        double u1 = 2.0F * buffer.readInt24() * Math.PI / 0xFFFFFF;
        byte unused = buffer.readByte();
        double u2 = 2.0F * buffer.readInt24() * Math.PI / 0xFFFFFF;
        float u3 = buffer.readShort() / 1000.0F;
        int fam = buffer.readInt24() / 0xFFFFFF;
        byte skin = buffer.readByte();
        int xPos = buffer.readInt24();
        int yPos = buffer.readInt24();
        byte nameLength = buffer.readByte();
        String nick = "";
        for (int i = 0; i < nameLength; i++) {
            nick += (char) buffer.readByte();
        }
        List<SnakePart> parts = new ArrayList<>();
        int partY = 0;
        int partX = 0;
        boolean head = true;
        while (buffer.hasNext()) {
            int prevPartX = partX;
            int prevPartY = partY;
            if (!head) {
                partX += (buffer.readByte() - 127) / 2;
                partY += (buffer.readByte() - 127) / 2;
            } else {
                partX = buffer.readInt24() / 5;
                partY = buffer.readInt24() / 5;
                prevPartX = partX;
                prevPartY = partY;
                head = false;
            }
            parts.add(new SnakePart(partX - prevPartX, partY - prevPartY));
        }
        Snake snake = new Snake(nick, Skin.values()[skin], snakeId, xPos, yPos, parts);
        client.addSnake(snake);
    }

    @Override
    public int getMessageId() {
        return 's';
    }
}
