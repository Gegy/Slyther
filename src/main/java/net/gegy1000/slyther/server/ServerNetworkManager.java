package net.gegy1000.slyther.server;

import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.MessageHandler;
import net.gegy1000.slyther.network.message.MessageSetup;
import net.gegy1000.slyther.network.message.SlytherClientMessageBase;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ServerNetworkManager extends WebSocketServer {
    private SlytherServer server;

    public ServerNetworkManager(SlytherServer server, int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        this.server = server;
        start();
    }

    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        System.out.println("Initiating new connection.");
        ConnectedClient client = new ConnectedClient(connection);
        server.clients.add(client);
        client.lastPacketTime = System.currentTimeMillis();
        send(client, new MessageSetup());
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
        ConnectedClient client = server.getConnectedClient(connection);
        if (client != null) {
            client.lastPacketTime = System.currentTimeMillis();
            MessageByteBuffer buffer = new MessageByteBuffer(byteBuffer);
            SlytherClientMessageBase message = MessageHandler.INSTANCE.getClientMessage(buffer);
            if (message != null) {
                message.read(buffer, server, client); //TODO Tasks
            } else {
                System.err.println("Received unknown message " + Arrays.toString(buffer.array()));
            }
        }
    }

    @Override
    public void onError(WebSocket connection, Exception e) {
        server.removeClient(connection);
    }

    public void send(ConnectedClient client, SlytherServerMessageBase message) {
        try {
            MessageByteBuffer buffer = new MessageByteBuffer();
            buffer.writeUInt16((int) (System.currentTimeMillis() - client.lastPacketTime));
            buffer.writeUInt8(message.getMessageIds()[0]); //TODO Select
            message.write(buffer, server);
            client.socket.send(buffer.bytes());
        } catch (Exception e) {
            System.err.println("An error occurred while sending message " + message.getClass().getName());
            e.printStackTrace();
        }
    }
}
