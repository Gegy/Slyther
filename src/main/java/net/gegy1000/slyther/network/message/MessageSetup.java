package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageSetup extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
        buffer.writeUInt24(server.configuration.GAME_RADIUS);
        buffer.writeUInt16(server.configuration.MSCPS);
        buffer.writeUInt16(server.configuration.SECTOR_SIZE);
        buffer.writeUInt16(server.configuration.SECTORS_ALONG_EDGE);
        buffer.writeUInt8((int) (server.configuration.SPANG_DV * 10));
        buffer.writeUInt16((int) (server.configuration.NSP_1 * 100));
        buffer.writeUInt16((int) (server.configuration.NSP_2 * 100));
        buffer.writeUInt16((int) (server.configuration.NSP_3 * 100));
        buffer.writeUInt16((int) (server.configuration.MAMU * 1000));
        buffer.writeUInt16((int) (server.configuration.MANU_2 * 1000));
        buffer.writeUInt16((int) (server.configuration.CST * 1000));
        buffer.writeUInt8(server.configuration.PROTOCOL_VERSION);
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
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
        int protocolVersion = buffer.readUInt8();

        client.setup(gameRadius, mscps, sectorSize, sectorCountAlongEdge, spangDV, nsp1, nsp2, nsp3, mamu, manu2, cst, protocolVersion);
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'a' };
    }
}
