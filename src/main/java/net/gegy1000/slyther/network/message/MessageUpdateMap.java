package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageUpdateMap extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        for (int x = 0; x < 80; x++) {
            for (int y = 0; y < 80; y++) {
                client.map[x][y] = false;
            }
        }
        int mapSize = 80 * 80;
        outer:
        for (int i = 0; i < mapSize && buffer.hasNext(); i++) {
            int value = buffer.readByte();
            if (value >= 128) {
                i += (value - 128);
            } else {
                for (int bit = 64; (value & bit) <= 0; bit /= 2) {
                    client.map[i % 80][i / 80] = true;
                    i++;
                    if (i >= mapSize || !(buffer.hasNext())) {
                        break outer;
                    }
                }
            }
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'u' };
    }
}
