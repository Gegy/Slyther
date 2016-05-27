package net.gegy1000.slyther.client;

import net.gegy1000.slyther.network.ServerListHandler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.*;

public class ServerPingManager extends WebSocketClient {
    private static final int PING_TIMEOUT = 20000;
    private ServerListHandler.Server server;
    private int pingCount = 0;
    private boolean closed;
    private long[] pings = new long[4];
    private long pingSendTime;

    public ServerPingManager(ServerListHandler.Server server) throws URISyntaxException {
        super(new URI("ws://" + server.getClusterIp() + ":80/ptc"), new Draft_17(), ClientNetworkManager.HEADERS, 0);
        this.server = server;
        connect();
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        if (ServerListHandler.INSTANCE.getPingedCount() < 10) {
            pingSendTime = System.currentTimeMillis();
            send(new byte[] { 'p' });
        } else {
            close();
        }
    }

    @Override
    public void onMessage(ByteBuffer buffer) {
        if (ServerListHandler.INSTANCE.getPingedCount() < 10) {
            if (buffer.get() == 'p') {
                if (pingCount < 4) {
                    long currentTime = System.currentTimeMillis();
                    pings[pingCount] = currentTime - pingSendTime;
                    pingSendTime = currentTime;
                    send(new byte[] { 'p' });
                    pingCount++;
                } else {
                    server.setPing(pings);
                    System.out.println(server.getClusterIp() + " has a ping time of " + server.getPing());
                    close();
                }
            }
        } else {
            close();
        }
    }

    @Override
    public void onMessage(String message) {
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        closed = true;
    }

    @Override
    public void onError(Exception ex) {
        closed = true;
    }

    public static void pingServers() throws IOException {
        ServerListHandler.INSTANCE.resetPingedServerCount();
        List<ServerListHandler.Server> servers = ServerListHandler.INSTANCE.getServerList();
        List<ServerPingManager> pingers = new ArrayList<>();
        if (servers != null && servers.size() > 0) {
            for (ServerListHandler.Server server : servers) {
                try {
                    pingers.add(new ServerPingManager(server));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        new Thread(() -> {
            long start = System.currentTimeMillis();
            if (System.currentTimeMillis() - start > ServerPingManager.PING_TIMEOUT) {
                for (ServerPingManager server : new ArrayList<>(pingers)) {
                    if (!server.closed) {
                        server.close();
                    }
                }
            }
        }).start();
    }
}
