package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.ProfanityHandler;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageUpdateLongestPlayer extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        int length = buffer.readShort();
        float fam = (float) buffer.readInt24() / 0xFFFFFF;
        int score = (int) Math.floor(15.0F * (client.getFPSL(length) + fam / client.getFMLT(length) - 1.0F) - 5.0F);
        String name = "";
        for (int i = 0; i < buffer.readByte(); i++) {
            name += (char) buffer.readByte();
        }
        if (!ProfanityHandler.INSTANCE.isClean(name)) {
            name = "";
        }
        String message = "";
        while (buffer.hasNext()) {
            message += (char) buffer.readByte();
        }
        if (!ProfanityHandler.INSTANCE.isClean(message)) {
            message = "";
        }
        client.longestPlayerName = name;
        client.longestPlayerScore = score;
        client.longestPlayerMessage = message;
        System.out.println(client.longestPlayerName + " (" + client.longestPlayerScore + ") - \"" + client.longestPlayerMessage + "\"");
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'm' };
    }
}
