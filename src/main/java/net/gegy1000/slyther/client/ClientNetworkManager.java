package net.gegy1000.slyther.client;

import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.MessageHandler;
import net.gegy1000.slyther.network.ServerHandler;
import net.gegy1000.slyther.network.message.MessageClientPing;
import net.gegy1000.slyther.network.message.MessageClientSetup;
import net.gegy1000.slyther.network.message.SlytherClientMessageBase;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClientNetworkManager extends WebSocketClient {
    private SlytherClient client;
    private String ip;

    private boolean isOpen = false;

    public int bytesPerSecond;

    public boolean isReplaying;
    public GameRecorder recorder;
    public GameReplayer replayer;

    public ClientNetworkManager(SlytherClient client, String ip, Map<String, String> headers, boolean shouldRecord, boolean isReplaying) throws URISyntaxException, IOException {
        super(new URI("ws://" + ip + "/slither"), new Draft_17(), headers, 0);
        this.ip = ip;
        this.client = client;
        this.isReplaying = isReplaying;
        if (this.isReplaying) {
            if (!SlytherClient.RECORD_FILE.exists()) {
                SlytherClient.RECORD_FILE.createNewFile();
            }
            replayer = new GameReplayer(SlytherClient.RECORD_FILE, this);
        } else {
            if (!SlytherClient.RECORD_FILE.delete()) {
                SlytherClient.RECORD_FILE.createNewFile();
            }
        }
        if (!isReplaying) {
            connect();
            if (shouldRecord) {
                recorder = new GameRecorder(SlytherClient.RECORD_FILE);
                recorder.start();
            }
        } else {
            this.isOpen = true;
        }
    }

    public static ClientNetworkManager create(SlytherClient client, ServerHandler.Server server, boolean shouldRecord) throws Exception {
        return create(client, server.getIp(), shouldRecord);
    }

    public static ClientNetworkManager create(SlytherClient client, String ip, boolean shouldRecord) throws Exception {
        System.out.println("Connecting to server " + ip);
        Map<String, String> headers = new HashMap<>(ServerHandler.INSTANCE.getHeaders());
        headers.put("Host", ip);
        return new ClientNetworkManager(client, ip, headers, shouldRecord, false);
    }

    public static ClientNetworkManager create(SlytherClient client) throws IOException, URISyntaxException {
        return new ClientNetworkManager(client, "", null, false, true);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        isOpen = true;
        send(new MessageClientSetup(client.configuration.nickname, client.configuration.skin));
        ping();
        System.out.println("Connected to " + ip);
    }

    public void ping() {
        if (isOpen && !isReplaying) {
            if (!client.wfpr) {
                send(new MessageClientPing());
                client.wfpr = true;
            }
        }
    }

    @Override
    public void onMessage(String message) {
    }

    @Override
    public void onMessage(ByteBuffer byteBuffer) {
        MessageByteBuffer buffer = new MessageByteBuffer(byteBuffer);
        if (recorder != null) {
            recorder.onMessage(byteBuffer.array());
        }
        if (buffer.limit() >= 2) {
            bytesPerSecond += buffer.limit();
            client.lastPacketTime = client.currentPacketTime;
            client.currentPacketTime = System.currentTimeMillis();
            int serverTimeDelta = buffer.readUInt16();
            byte messageId = (byte) buffer.readUInt8();
            long timeDelta = client.currentPacketTime - client.lastPacketTime;
            if (client.lastPacketTime == 0) {
                timeDelta = 0;
            }
            client.packetTimeOffset += timeDelta - serverTimeDelta;
            client.etm += Math.max(-180, Math.min(180, timeDelta - serverTimeDelta));
            Class<? extends SlytherServerMessageBase> messageType = MessageHandler.INSTANCE.getServerMessage(messageId);
            if (messageType != null) {
                try {
                    SlytherServerMessageBase message = messageType.getConstructor().newInstance();
                    message.messageId = messageId;
                    message.serverTimeDelta = serverTimeDelta;
                    client.scheduleTask(() -> {
                        message.read(buffer, client);
                        return null;
                    });
                } catch (Exception e) {
                    System.err.println("Error while receiving message " + messageId + "!" + " (" + (char) messageId + ")");
                    e.printStackTrace();
                }
            } else {
                System.err.println("Received unknown message " + messageId + "!" + " (" + (char) messageId + ") " + Arrays.toString(buffer.array()));
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed with code " + code + " for reason \"" + reason + "\"");
        isOpen = false;
        client.reset();
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        isOpen = false;
        client.reset();
    }

    public void send(SlytherClientMessageBase message) {
        if (isOpen && !isReplaying) {
            try {
                MessageByteBuffer buffer = new MessageByteBuffer();
                message.write(buffer, client);
                send(buffer.bytes());
            } catch (Exception e) {
                System.err.println("An error occurred while sending message " + message.getClass().getName());
                e.printStackTrace();
            }
        }
    }

    public void tick() {
        if (isReplaying) {
            try {
                if (!replayer.tick()) {
                    onClose(0, "Finished Playback", false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
