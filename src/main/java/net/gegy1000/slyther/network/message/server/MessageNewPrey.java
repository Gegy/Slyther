package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.ClientNetworkManager;
import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.game.entity.ClientPrey;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.entity.Prey;
import net.gegy1000.slyther.game.entity.Snake;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageNewPrey extends SlytherServerMessageBase {
    private Prey<?> prey;

    public MessageNewPrey(Prey<?> prey) {
        this.prey = prey;
    }

    public MessageNewPrey() {
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        //TODO
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client, ClientNetworkManager networkManager) {
        int id = buffer.readUInt16();
        if (!buffer.hasRemaining()) {
            Prey prey = client.getPrey(id);
            client.removeEntity(prey);
        } else if (buffer.hasExactlyRemaining(2)) {
            ClientPrey prey = client.getPrey(id);
            if (prey != null) {
                Snake eater = client.getSnake(buffer.readUInt16());
                prey.eaten = true;
                prey.eater = eater;
                if (eater != null) {
                    prey.eatenFR = 0;
                } else {
                    client.removeEntity(prey);
                }
            }
        } else {
            Color color = Color.values()[buffer.readUInt8()];
            float x = buffer.readUInt24() / 5.0F;
            float y = buffer.readUInt24() / 5.0F;
            float size = buffer.readUInt8() / 5.0F;
            int turningDirection = buffer.readUInt8() - 48;
            float wantedAngle = (float) (2.0F * buffer.readUInt24() * Math.PI / 0xFFFFFF);
            float angle = (float) (2.0F * buffer.readUInt24() * Math.PI / 0xFFFFFF);
            float speed = buffer.readUInt16() / 1000.0F;
            client.addEntity(new ClientPrey(client, id, x, y, size, color, turningDirection, wantedAngle, angle, speed));
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'y' };
    }
}
