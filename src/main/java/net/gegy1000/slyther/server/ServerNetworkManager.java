package net.gegy1000.slyther.server;

import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.MessageHandler;
import net.gegy1000.slyther.network.NetworkManager;
import net.gegy1000.slyther.network.message.SlytherClientMessageBase;
import net.gegy1000.slyther.util.Log;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ServerNetworkManager extends WebSocketServer implements NetworkManager {
    private SlytherServer server;
    private int port;

    private int currentClientId;

    public ServerNetworkManager(SlytherServer server, int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        this.server = server;
        this.port = port;
        start();
    }

    @Override
    public void run() {
        Thread.currentThread().setName(getClass().getSimpleName());
        Log.info("Server started on port {}", port);
        super.run();
    }

    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        server.scheduleTask(() -> {
            Log.debug("Initiating a new connection.");
            ConnectedClient client = new ConnectedClient(server, connection, currentClientId);
            if (!server.clients.contains(client)) {
                currentClientId++;
                server.clients.add(client);
                client.lastPacketTime = System.currentTimeMillis();
            }
            return null;
        });
    }

    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote) {
        server.removeClient(connection);
    }

    @Override
    public void onMessage(WebSocket connection, String message) {
    }

    @Override
    public void onMessage(WebSocket connection, ByteBuffer byteBuffer) {
        server.scheduleTask(() -> {
            ConnectedClient client = server.getConnectedClient(connection);
            if (client != null) {
                MessageByteBuffer buffer = new MessageByteBuffer(byteBuffer);
                SlytherClientMessageBase message = MessageHandler.INSTANCE.getClientMessage(buffer);
                if (message == null) {
                    Log.warn("Received unknown message {} from {} ({})", () -> Arrays.toString(buffer.array()), client.name, client.id);
                } else {
                    message.read(buffer, server, client);
                }
            }
            return null;
        });
    }

    @Override
    public void onError(WebSocket connection, Exception e) {
        server.removeClient(connection);
    }
}
