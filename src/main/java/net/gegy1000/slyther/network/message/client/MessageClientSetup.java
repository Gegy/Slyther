package net.gegy1000.slyther.network.message.client;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.ProfanityHandler;
import net.gegy1000.slyther.game.Skin;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherClientMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;
import net.gegy1000.slyther.util.Log;

public class MessageClientSetup extends SlytherClientMessageBase {
    private String username;
    private Skin skin;

    public MessageClientSetup() {
    }

    public MessageClientSetup(String username, Skin skin) {
        this.username = username;
        this.skin = skin;
        if (this.username.length() > 24) {
            this.username = this.username.substring(0, 24);
        }
        if (!ProfanityHandler.isClean(this.username)) {
            this.username = "";
        }
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherClient client) {
        buffer.writeUInt8('s');
        buffer.writeUInt8(10 - 1);
        buffer.writeUInt8(skin.ordinal());
        buffer.writeASCIIBytes(username);
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        int protocolVersion = buffer.readUInt8() + 1;
        Skin skin = Skin.values()[buffer.readUInt8() % Skin.values().length];
        String name = buffer.readASCIIBytes();
        if (!ProfanityHandler.isClean(name)) {
            name = "";
        }
        client.setup(name, skin, protocolVersion);
        Log.debug("{} ({}) connected with skin {}", client.name, client.id, client.skin);
    }
}