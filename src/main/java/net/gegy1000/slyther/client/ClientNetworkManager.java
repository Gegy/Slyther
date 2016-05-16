package net.gegy1000.slyther.client;

import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.MessageHandler;
import net.gegy1000.slyther.network.message.MessageSetUsername;
import net.gegy1000.slyther.network.message.SlytherClientMessageBase;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.game.Skin;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.*;

public class ClientNetworkManager extends WebSocketClient {
    private SlytherClient client;
    private String ip;

    private boolean isOpen = false;

    public static final Map<String, String> HEADERS = new HashMap<>();

    static {
        HEADERS.put("Origin", "http://slither.io");
        HEADERS.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
        HEADERS.put("Accept-Language", "en-US,en;q=0.8");
        HEADERS.put("Cache-Control", "no-cache");
        HEADERS.put("Connection", "Upgrade");
        HEADERS.put("Pragma", "no-cache");
    }

    public ClientNetworkManager(SlytherClient client, String ip, Map<String, String> headers) throws URISyntaxException {
        super(new URI("ws://" + ip + "/slither"), new Draft_17(), headers, 0);
        this.ip = ip;
        this.client = client;
        this.connect();
    }

    public static ClientNetworkManager create(SlytherClient client) throws Exception {
        String bestServer = ServerPingManager.getBestServer();
        if (bestServer != null) {
            System.out.println("Connecting to server " + bestServer);
            Map<String, String> headers = new HashMap<>(HEADERS);
            headers.put("Host", bestServer);
            return new ClientNetworkManager(client, bestServer, headers);
        }
        return null;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        this.isOpen = true;
        this.send(new MessageSetUsername(client.nickname, Skin.RAINBOW));
        this.ping();
        System.out.println("Connected to " + ip);
    }

    public void ping() {
        if (!client.wfpr) {
            this.send(new byte[] { 'p' });
            this.client.wfpr = true;
        }
    }

    @Override
    public void onMessage(String message) {
    }

    @Override
    public void onMessage(ByteBuffer byteBuffer) {
        MessageByteBuffer buffer = new MessageByteBuffer(byteBuffer.order(MessageByteBuffer.BYTE_ORDER).array());
        if (buffer.length() >= 2) {
            client.lastPacketTime = client.currentPacketTime;
            client.currentPacketTime = System.currentTimeMillis();
            short serverTimeDelta = buffer.readShort();
            byte messageId = buffer.readByte();
            long timeDelta = client.currentPacketTime - client.lastPacketTime;
            if (client.lastPacketTime == 0) {
                timeDelta = 0;
            }
            client.packetTimeOffset += timeDelta - serverTimeDelta;
            Class<? extends SlytherServerMessageBase> messageType = MessageHandler.INSTANCE.getServerMessage(messageId);
            if (messageType != null) {
                try {
                    SlytherServerMessageBase message = messageType.getConstructor().newInstance();
                    message.messageId = messageId;
                    message.serverTimeDelta = serverTimeDelta;
                    message.readBase(buffer, this.client);
                } catch (Exception e) {
                    System.err.println("Error while receiving message " + messageId + "!" + " (" + (char) messageId + ")");
                    e.printStackTrace();
                }
            } else {
                System.err.println("Received unknown message " + messageId + "!" + " (" + (char) messageId + ")");
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed with code " + code + " for reason \"" + reason + "\"");
        this.isOpen = false;
        System.exit(-1);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        System.exit(-1);
    }

    public void send(SlytherClientMessageBase message) {
        if (this.isOpen) {
            try {
                MessageByteBuffer buffer = new MessageByteBuffer();
                message.write(buffer, client);
                this.send(buffer.toBytes());
            } catch (Exception e) {
                System.err.println("An error occurred while sending message " + message.getClass().getName());
                e.printStackTrace();
            }
        }
    }
}
