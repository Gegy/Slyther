package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageSetup extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
        buffer.writeInt24(SlytherServer.GAME_RADIUS);
        buffer.writeShort(SlytherServer.MSCPS);
        buffer.writeShort(SlytherServer.SECTOR_SIZE);
        buffer.writeShort(SlytherServer.SECTORS_ALONG_EDGE);
        buffer.writeByte((byte) (SlytherServer.SPANG_DV * 10));
        buffer.writeShort((short) (SlytherServer.NSP_1 * 100));
        buffer.writeShort((short) (SlytherServer.NSP_2 * 100));
        buffer.writeShort((short) (SlytherServer.NSP_3 * 100));
        buffer.writeShort((short) (SlytherServer.MAMU * 1000));
        buffer.writeShort((short) (SlytherServer.MANU_2 * 1000));
        buffer.writeShort((short) (SlytherServer.CST * 1000));
        buffer.writeByte(SlytherServer.PROTOCOL_VERSION);
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        int gameRadius = buffer.readInt24();
        short mscps = buffer.readShort();
        short sectorSize = buffer.readShort();
        short sectorCountAlongEdge = buffer.readShort();
        float spangDV = buffer.readByte() / 10.0F;
        float nsp1 = buffer.readShort() / 100.0F;
        float nsp2 = buffer.readShort() / 100.0F;
        float nsp3 = buffer.readShort() / 100.0F;
        float mamu = buffer.readShort() / 1000.0F;
        float manu2 = buffer.readShort() / 1000.0F;
        float cst = buffer.readShort() / 1000.0F;
        int protocolVersion = buffer.readByte();

        client.setup(gameRadius, mscps, sectorSize, sectorCountAlongEdge, spangDV, nsp1, nsp2, nsp3, mamu, manu2, cst, protocolVersion);
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'a' };
    }
}
