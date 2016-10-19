package net.gegy1000.slyther.client;

import net.gegy1000.slyther.client.recording.GameRecorder;
import net.gegy1000.slyther.client.recording.GameReplayer;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.MessageHandler;
import net.gegy1000.slyther.network.NetworkManager;
import net.gegy1000.slyther.network.ServerHandler;
import net.gegy1000.slyther.network.message.SlytherClientMessageBase;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.network.message.client.MessageStartLogin;
import net.gegy1000.slyther.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class ClientNetworkManager extends WebSocketClient implements NetworkManager {
    public static final int SHUTDOWN_CODE = 1;

    public static final byte[] PING_DATA = new byte[] { (byte) 251 };
    private SlytherClient client;
    private String ip;

    public int bytesPerSecond;
    public int packetsPerSecond;

    public boolean isReplaying;
    public GameRecorder recorder;

    public boolean waitingForPingReturn;
    public long lastPacketTime;
    public long lastPingSendTime;

    public long packetTimeOffset;

    public long currentPacketTime;

    public ClientNetworkManager(URI uri, SlytherClient client, String ip, Map<String, String> headers, boolean shouldRecord, boolean isReplaying) throws IOException {
        super(uri, new Draft_17(), headers, 0);
        this.ip = ip;
        this.client = client;
        this.isReplaying = isReplaying;
        if (!isReplaying && shouldRecord && !SlytherClient.RECORD_FILE.delete()) {
            SlytherClient.RECORD_FILE.createNewFile();
        }
        connect();
        if (shouldRecord) {
            recorder = new GameRecorder(SlytherClient.RECORD_FILE);
            recorder.start();
        }
    }

    public static ClientNetworkManager create(SlytherClient client, ServerHandler.Server server, boolean shouldRecord) throws Exception {
        return create(client, server.getIp(), shouldRecord);
    }

    public static ClientNetworkManager create(SlytherClient client, String ip, boolean shouldRecord) throws Exception {
        Log.info("Connecting to server {}", ip);
        Map<String, String> headers = new HashMap<>(ServerHandler.INSTANCE.getHeaders());
        headers.put("Host", ip);
        return new ClientNetworkManager(new URI("ws://" + ip + "/slither"), client, ip, headers, shouldRecord, false);
    }

    public static ClientNetworkManager create(SlytherClient client) throws IOException, URISyntaxException {
        GameReplayer replayer = new GameReplayer(SlytherClient.RECORD_FILE);
        return new ClientNetworkManager(replayer.getURI(), client, "GameReplayer", null, false, true);
    }

    @Override
    public void run() {
        Thread.currentThread().setName(getClass().getSimpleName());
        super.run();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        send(new MessageStartLogin());
        Log.info("Connected to {}", ip);
        lastPacketTime = System.currentTimeMillis();
    }

    public void ping() {
        if (isOpen() && !isReplaying) {
            if (!waitingForPingReturn) {
                send(PING_DATA);
                waitingForPingReturn = true;
            }
        }
    }

    @Override
    public void onMessage(String message) {}

    @Override
    public void onMessage(ByteBuffer byteBuffer) {
        MessageByteBuffer buffer = new MessageByteBuffer(byteBuffer);
        if (recorder != null) {
            recorder.onMessage(buffer.array());
        }
        if (buffer.limit() >= 2) {
            bytesPerSecond += buffer.limit();
            packetsPerSecond++;
            lastPacketTime = currentPacketTime;
            currentPacketTime = System.currentTimeMillis();
            int serverTimeDelta = buffer.readUInt16();
            byte messageId = (byte) buffer.readUInt8();
            long timeDelta = currentPacketTime - lastPacketTime;
            if (lastPacketTime == 0) {
                timeDelta = 0;
            }
            packetTimeOffset = serverTimeDelta - timeDelta;
            client.errorTime += Math.max(-180, Math.min(180, timeDelta - serverTimeDelta));
            Class<? extends SlytherServerMessageBase> messageType = MessageHandler.INSTANCE.getServerMessage(messageId);
            if (messageType == null) {
                Log.warn("Received unknown message {} ({})!", () -> Log.bytes(buffer.array()), (char) messageId);
            } else {
                try {
                    SlytherServerMessageBase message = messageType.getConstructor().newInstance();
                    message.messageId = messageId;
                    message.serverTimeDelta = serverTimeDelta;
                    client.scheduleTask(() -> {
                        if (isOpen()) {
                            message.read(buffer, client, this);
                        }
                        return null;
                    });
                } catch (Exception e) {
                    Log.error("Error while receiving message " + messageId + "!" + " (" + (char) messageId + ")");
                    Log.catching(e);
                }
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.info("Connection closed with code {} for reason \"{}\"", code, reason);
        if (code != SHUTDOWN_CODE) {
            client.reset();   
        }
        if (recorder != null) {
            recorder.close();
        }
    }

    @Override
    public void onError(Exception e) {
        Log.catching(e);
    }

    public void send(SlytherClientMessageBase message) {
        if (isOpen() && !isReplaying) {
            try {
                MessageByteBuffer buffer = new MessageByteBuffer();
                message.write(buffer, client);
                send(buffer.bytes());
            } catch (Exception e) {
                Log.error("An error occurred while sending message {}", message.getClass().getName());
                Log.catching(e);
            }
        }
    }

    public String getIp() {
        return ip;
    }
}
