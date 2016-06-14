package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.ClientNetworkManager;
import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageSetup extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        buffer.writeUInt24(server.configuration.gameRadius);
        buffer.writeUInt16(server.configuration.mscps);
        buffer.writeUInt16(server.configuration.sectorSize);
        buffer.writeUInt16(server.configuration.sectorsAlongEdge);
        buffer.writeUInt8((int) (server.configuration.spangDv * 10));
        buffer.writeUInt16((int) (server.configuration.nsp1 * 100));
        buffer.writeUInt16((int) (server.configuration.nsp2 * 100));
        buffer.writeUInt16((int) (server.configuration.nsp3 * 100));
        buffer.writeUInt16((int) (server.configuration.snakeTurnSpeed * 1000));
        buffer.writeUInt16((int) (server.configuration.preyTurnSpeed * 1000));
        buffer.writeUInt16((int) (server.configuration.cst * 1000));
        buffer.writeUInt8(client.protocolVersion - 1);
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client, ClientNetworkManager networkManager) {
        int gameRadius = buffer.readUInt24();
        int mscps = buffer.readUInt16();
        int sectorSize = buffer.readUInt16();
        int sectorCountAlongEdge = buffer.readUInt16();
        float spangDV = buffer.readUInt8() / 10.0F;
        float nsp1 = buffer.readUInt16() / 100.0F;
        float nsp2 = buffer.readUInt16() / 100.0F;
        float nsp3 = buffer.readUInt16() / 100.0F;
        float mamu = buffer.readUInt16() / 1000.0F;
        float manu2 = buffer.readUInt16() / 1000.0F;
        float cst = buffer.readUInt16() / 1000.0F;
        int protocolVersion = buffer.readUInt8() + 1;

        client.setup(gameRadius, mscps, sectorSize, sectorCountAlongEdge, spangDV, nsp1, nsp2, nsp3, mamu, manu2, cst, protocolVersion);
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'a' };
    }
}
