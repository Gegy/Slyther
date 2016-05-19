package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Prey;
import net.gegy1000.slyther.game.Snake;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

import java.util.Arrays;

public class MessageNewPrey extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        int id = buffer.readShort();
        if (!buffer.hasNext()) {
            Prey prey = client.getPrey(id);
            client.preys.remove(prey);
        } else if (buffer.hasNext(2)) {
            Prey prey = client.getPrey(id);
            if (prey != null) {
                Snake eater = client.getSnake(buffer.readShort());
                prey.eaten = true;
                prey.eater = eater;
                if (eater != null) {
                    prey.eatenFR = 0;
                } else {
                    client.preys.remove(prey);
                }
            }
        } else if (buffer.hasNext(17)) {
            int cv = buffer.readByte();
            float x = buffer.readInt24() / 5.0F;
            float y = buffer.readInt24() / 5.0F;
            float size = buffer.readByte() / 5.0F;
            int dir = buffer.readByte() - 48;
            float wang = (float) (2.0F * buffer.readInt24() * Math.PI / 0xFFFFFF);
            float ang = (float) (2.0F * buffer.readInt24() * Math.PI / 0xFFFFFF);
            float sp = buffer.readShort() / 1000.0F;
            Prey prey = new Prey(client, id, x, y, size, cv, dir, wang, ang, sp);
            client.preys.add(prey);
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'y' };
    }
}
