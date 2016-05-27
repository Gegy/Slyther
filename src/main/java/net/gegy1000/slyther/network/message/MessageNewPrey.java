package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.Prey;
import net.gegy1000.slyther.game.Snake;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageNewPrey extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        int id = buffer.readUInt16();
        if (!buffer.hasRemaining()) {
            Prey prey = client.getPrey(id);
            client.preys.remove(prey);
        } else if (buffer.hasExactlyRemaining(2)) {
            Prey prey = client.getPrey(id);
            if (prey != null) {
                Snake eater = client.getSnake(buffer.readUInt16());
                prey.eaten = true;
                prey.eater = eater;
                if (eater != null) {
                    prey.eatenFR = 0;
                } else {
                    client.preys.remove(prey);
                }
            }
        } else {
            Color cv = Color.values()[buffer.readUInt8()];
            float x = buffer.readUInt24() / 5.0F;
            float y = buffer.readUInt24() / 5.0F;
            float size = buffer.readUInt8() / 5.0F;
            int dir = buffer.readUInt8() - 48;
            float wang = (float) (2.0F * buffer.readUInt24() * Math.PI / 0xFFFFFF);
            float ang = (float) (2.0F * buffer.readUInt24() * Math.PI / 0xFFFFFF);
            float sp = buffer.readUInt16() / 1000.0F;
            Prey prey = new Prey(client, id, x, y, size, cv, dir, wang, ang, sp);
            client.preys.add(prey);
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'y' };
    }
}
