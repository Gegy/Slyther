package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.LeaderboardEntry;
import net.gegy1000.slyther.game.ProfanityHandler;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageUpdateLeaderboard extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        client.wumsts = true;
        int playerIndex = buffer.read();
        client.rank = buffer.readShort();
        if (client.rank < client.bestRank) {
            client.bestRank = client.rank;
        }
        client.snakeCount = buffer.readShort();
        client.leaderboard.clear();
        int index = 1;
        while (buffer.hasRemaining()) {
            int length = buffer.readShort();
            float fam = (float) buffer.readInt24() / 0xFFFFFF;
            Color color = Color.values()[buffer.read() % 9];
            String name = "";
            int nameLength = buffer.read();
            for (int i = 0; i < nameLength; i++) {
                name += (char) buffer.read();
            }
            if (index != playerIndex) {
                if (!ProfanityHandler.INSTANCE.isClean(name)) {
                    name = "";
                }
            }
            int score = (int) Math.floor(15.0F * (client.getFPSL(length) + fam / client.getFMLT(length) - 1.0F) - 5.0F);
            client.leaderboard.add(index - 1, new LeaderboardEntry(name, score, color));
            index++;
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'l' };
    }
}
