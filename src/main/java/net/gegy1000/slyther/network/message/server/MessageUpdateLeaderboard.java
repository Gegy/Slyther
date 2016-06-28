package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.ClientNetworkManager;
import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.LeaderboardEntry;
import net.gegy1000.slyther.game.ProfanityHandler;
import net.gegy1000.slyther.game.SkinHandler;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;
import net.gegy1000.slyther.server.game.entity.ServerSnake;

public class MessageUpdateLeaderboard extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        int playerIndex = 0;
        if (client.rank > 0 && client.rank <= server.leaderboard.size()) {
            playerIndex = client.rank;
        }
        buffer.writeUInt8(playerIndex);
        buffer.writeUInt16(client.rank);
        buffer.writeUInt16(server.getSnakes().size());
        for (LeaderboardEntry leaderboardEntry : server.leaderboard) {
            ServerSnake snake = leaderboardEntry.client.snake;
            buffer.writeUInt16(snake.sct);
            buffer.writeUInt24((int) (snake.fam * 0xFFFFFF));
            buffer.writeUInt8(SkinHandler.INSTANCE.getDetails(snake.client.skin).pattern[0].ordinal() % Color.values().length);
            String name = leaderboardEntry.client.name;
            buffer.writeUInt8(name.length());
            for (int i = 0; i < name.length(); i++) {
                buffer.writeUInt8((byte) name.charAt(i));
            }
        }
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client, ClientNetworkManager networkManager) {
        int playerIndex = buffer.readUInt8();
        client.rank = buffer.readUInt16();
        if (client.rank < client.bestRank) {
            client.bestRank = client.rank;
        }
        client.snakeCount = buffer.readUInt16();
        client.leaderboard.clear();
        int index = 1;
        while (buffer.hasRemaining()) {
            int length = buffer.readUInt16();
            float fam = (float) buffer.readUInt24() / 0xFFFFFF;
            Color color = Color.values()[buffer.readUInt8() % 9];
            String name = "";
            int nameLength = buffer.readUInt8();
            for (int i = 0; i < nameLength; i++) {
                name += (char) buffer.readUInt8();
            }
            if (index != playerIndex) {
                if (!ProfanityHandler.isClean(name)) {
                    name = "";
                }
            }
            int score = (int) Math.floor(15.0F * (client.getFPSL(length) + fam / client.getFMLT(length) - 1.0F) - 5.0F);
            client.leaderboard.add(index - 1, new LeaderboardEntry(name, score, color, index == playerIndex));
            index++;
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'l' };
    }
}
