package net.gegy1000.slyther.client;

import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.MessageHandler;
import net.gegy1000.slyther.network.ServerListHandler;
import net.gegy1000.slyther.network.message.MessageSetUsername;
import net.gegy1000.slyther.network.message.SlytherClientMessageBase;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.game.Skin;
import org.java_websocket.client.WebSocketClient;
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

    private static final int PING_TIMEOUT = 20000;
    private static volatile ClientNetworkManager SELECTED_SERVER;

    private boolean pinging;
    private long pingStartTime;

    static {
        HEADERS.put("Origin", "http://slither.io");
        HEADERS.put("User-Agent", "Slyther");
    }

    public ClientNetworkManager(SlytherClient client, String ip, boolean pinging) throws URISyntaxException {
        super(new URI("ws://" + ip + "/slither"), new Draft_17(), HEADERS, 0);
        this.ip = ip;
        this.client = client;
        this.pinging = pinging;
        this.connect();
    }

    public static ClientNetworkManager create(SlytherClient client) throws Exception {
        List<String> serverList = ServerListHandler.INSTANCE.getServerList();
        if (serverList.size() > 0) {
            List<ClientNetworkManager> pinging = new ArrayList<>();
            for (String server : serverList) {
                pinging.add(new ClientNetworkManager(client, server, true));
            }
            long pingStartTime = System.currentTimeMillis();
            while (SELECTED_SERVER == null && System.currentTimeMillis() - pingStartTime < PING_TIMEOUT);
            for (ClientNetworkManager pinger : pinging) {
                if (pinger != SELECTED_SERVER) {
                    pinger.close();
                }
            }
            if (SELECTED_SERVER == null) {
                System.err.println("Failed to find server to join.");
                return null;
            }
            SELECTED_SERVER.initiate();
            return SELECTED_SERVER;
        }
        return null;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        if (this.pinging) {
            pingStartTime = System.currentTimeMillis();
            this.send(new MessageSetUsername("", Skin.BLUE_DEFAULT));
            this.send(new byte[] { 112 });
        } else {
            this.initiate();
        }
    }

    private void initiate() {
        this.pinging = false;
        this.isOpen = true;
        System.out.println("Connected to " + ip);
    }

    @Override
    public void onMessage(String message) {
    }

    @Override
    public void onMessage(ByteBuffer byteBuffer) {
        if (this.pinging) {
            if (SELECTED_SERVER == null) {
                SELECTED_SERVER = this;
                long time = System.currentTimeMillis() - pingStartTime;
                System.out.println(ip + " responded to ping in " + time + " millis");
            }
        }
        MessageByteBuffer buffer = new MessageByteBuffer(byteBuffer.order(MessageByteBuffer.BYTE_ORDER).array());
        if (buffer.toBytes().length > 4) {
            short timeSinceLastMessage = buffer.readShort();
            byte messageId = buffer.readByte();
            Class<? extends SlytherServerMessageBase> messageType = MessageHandler.INSTANCE.getServerMessage(messageId);
            if (messageType != null) {
                try {
                    SlytherServerMessageBase message = messageType.getConstructor().newInstance();
                    message.messageType = messageId;
                    message.timeSinceLastMessage = timeSinceLastMessage;
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
        if (!this.pinging) {
            System.out.println("Connection closed with code " + code + " for reason \"" + reason + "\"");
            this.isOpen = false;
            System.exit(-1);
        }
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        System.exit(-1);
    }

    public void send(SlytherClientMessageBase message) {
        if (this.isOpen || this.pinging) {
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
