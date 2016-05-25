package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Prey;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessagePreyPositionUpdate extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        int id = buffer.readUInt16();
        int x = buffer.readUInt16() * 3 + 1;
        int y = buffer.readUInt16() * 3 + 1;
        Prey prey = client.getPrey(id);
        if (prey != null) {
            float moveAmount = (client.etm / 8.0F * prey.sp / 4.0F) * client.lagMultiplier;
            float prevX = prey.posX;
            float prevY = prey.posY;
            if (buffer.hasRemaining(9)) {
                prey.dir = buffer.readUInt8() - 48;
                prey.ang = (float) (buffer.readUInt24() * Math.PI * 2.0F / 0xFFFFFF);
                prey.wang = (float) (buffer.readUInt24() * Math.PI * 2.0F / 0xFFFFFF);
                prey.sp = buffer.readUInt16() / 1000.0F;
            } else if (buffer.hasRemaining(5)) {
                prey.ang = (float) (buffer.readUInt24() * Math.PI * 2.0F / 0xFFFFFF);
                prey.sp = buffer.readUInt16() / 1000.0F;
            } else if (buffer.hasRemaining(6)) {
                prey.dir = buffer.readUInt8() - 48;
                prey.wang = (float) (buffer.readUInt24() * Math.PI * 2.0F / 0xFFFFFF);
                prey.sp = buffer.readUInt16() / 1000.0F;
            } else if (buffer.hasRemaining(7)) {
                prey.dir = buffer.readUInt8() - 48;
                prey.ang = (float) (buffer.readUInt24() * Math.PI * 2.0F / 0xFFFFFF);
                prey.wang = (float) (buffer.readUInt24() * Math.PI * 2.0F / 0xFFFFFF);
            } else if (buffer.hasRemaining(3)) {
                prey.ang = (float) (buffer.readUInt24() * Math.PI * 2.0F / 0xFFFFFF);
            } else if (buffer.hasRemaining(4)) {
                prey.dir = buffer.readUInt8() - 48;
                prey.wang = (float) (buffer.readUInt24() * Math.PI * 2.0F / 0xFFFFFF);
            } else if (buffer.hasRemaining(2)) {
                prey.sp = buffer.readUInt16() / 1000.0F;
            }
            prey.posX = (float) (x + Math.cos(prey.ang) * moveAmount);
            prey.posY = (float) (y + Math.sin(prey.ang) * moveAmount);
            float moveX = prey.posX - prevX;
            float moveY = prey.posY - prevY;
            int fpos = prey.fpos;
            for (int i = 0; i < SlytherClient.RFC; i++) {
                prey.fxs[fpos] -= moveX * SlytherClient.RFAS[i];
                prey.fys[fpos] -= moveY * SlytherClient.RFAS[i];
                fpos++;
                if (fpos >= SlytherClient.RFC) {
                    fpos = 0;
                }
            }
            prey.fx = prey.fxs[prey.fpos];
            prey.fy = prey.fys[prey.fpos];
            prey.ftg = SlytherClient.RFC;
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'j' };
    }
}
