package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.ProfanityHandler;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;
import net.gegy1000.slyther.game.Skin;

public class MessageSetUsername extends SlytherClientMessageBase {
    private String username;
    private Skin skin;

    public MessageSetUsername(String username, Skin skin) {
        this.username = username;
        this.skin = skin;
        if (this.username.length() > 24) {
            this.username = this.username.substring(0, 24);
        }
        if (!ProfanityHandler.INSTANCE.isClean(this.username)) {
            this.username = "";
        }
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherClient client) {
        buffer.writeByte((byte) 's');
        buffer.writeByte((byte) 8);
        buffer.writeByte((byte) skin.ordinal());
        buffer.writeEndStr8(username);
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherServer server) {
        // TODO
    }
}