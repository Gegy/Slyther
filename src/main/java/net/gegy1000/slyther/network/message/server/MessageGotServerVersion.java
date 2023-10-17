package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.ClientNetworkManager;
import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.network.message.client.MessageClientRiddleAnswer;
import net.gegy1000.slyther.network.message.client.MessageClientSetup;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageGotServerVersion extends SlytherServerMessageBase {
    private String version;

    public MessageGotServerVersion() {
    }

    public MessageGotServerVersion(String version) {
        this.version = version;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        buffer.writeASCIIBytes(version);
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client, ClientNetworkManager networkManager) {
        networkManager.send(decodeSecret(buffer.array()));
        byte[] initRequest = new byte[4 + client.configuration.nickname.length()];
        initRequest[0] = 115;
        initRequest[1] = 10;
        initRequest[2] = (byte) client.configuration.skin.ordinal();
        initRequest[3] = (byte) client.configuration.nickname.length();
        for (int i = 0; i < client.configuration.nickname.length(); i++) {
            initRequest[4 + i] = (byte) client.configuration.nickname.codePointAt(i);
        }
        networkManager.send(initRequest);
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { '6' };
    }

    private byte[] decodeSecret(byte[] data) {
        
        int[] secret = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            secret[i] = data[i] & 0xFF;
        }

        byte[] result = new byte[24];

        int globalValue = 0;
        for (int i = 0; i < 24; i++) {
            int value1 = secret[17 + i * 2];
            if (value1 <= 96) {
                value1 += 32;
            }
            value1 = (value1 - 98 - i * 34) % 26;
            if (value1 < 0) {
                value1 += 26;
            }

            int value2 = secret[18 + i * 2];
            if (value2 <= 96) {
                value2 += 32;
            }
            value2 = (value2 - 115 - i * 34) % 26;
            if (value2 < 0) {
                value2 += 26;
            }

            int interimResult = (value1 << 4) | value2;
            int offset = interimResult >= 97 ? 97 : 65;
            interimResult -= offset;
            if (i == 0) {
                globalValue = 2 + interimResult;
            }
            result[i] = (byte) ((interimResult + globalValue) % 26 + offset);
            globalValue += 3 + interimResult;
        }

        return result;
    }
}
