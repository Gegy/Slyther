package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Snake;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessagePlayerDeath extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        int type = buffer.read();
        Snake player = client.player;
        System.out.println("Final length: " + (int) Math.floor(15.0 * (client.getFPSL(player.sct) + player.fam / client.getFMLT(player.sct) - 1.0) - 5.0));
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'v' };
    }
}
