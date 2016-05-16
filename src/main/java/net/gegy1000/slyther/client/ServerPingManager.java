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
    private static final int PING_TIMEOUT = 10000;
    private static volatile List<ServerPingManager> PINGERS = new ArrayList<>();
    private static volatile ServerPingManager BEST_SERVER;
    private String ip;
    private int pingCount = 0;
    private boolean closed;

    public ServerPingManager(String ip) throws URISyntaxException {
        super(new URI("ws://" + ip + ":80/ptc"), new Draft_17(), ClientNetworkManager.HEADERS, 0);
        this.ip = ip;
        this.connect();
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        if (BEST_SERVER == null) {
            PINGERS.add(this);
            this.send(new byte[] { 'p' });
        } else {
            this.close();
        }
    }

    @Override
    public void onMessage(ByteBuffer buffer) {
        if (BEST_SERVER == null) {
            if (buffer.get() == 'p') {
                if (this.pingCount < 4) {
                    this.send(new byte[] { 'p' });
                    this.pingCount++;
                } else {
                    System.out.println(ip + " pinged " + pingCount + " times");
                    BEST_SERVER = this;
                }
            }
        } else {
            this.close();
        }
    }

    @Override
    public void onMessage(String message) {
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        this.closed = true;
    }

    @Override
    public void onError(Exception ex) {
        this.closed = true;
    }

    public static String getBestServer() throws IOException {
        PINGERS.clear();
        Map<String, List<String>> servers = ServerListHandler.INSTANCE.getServers();
        if (servers.size() > 0) {
            long time = System.currentTimeMillis();
            for (Map.Entry<String, List<String>> cluster : servers.entrySet()) {
                try {
                    new ServerPingManager(cluster.getKey());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            while (BEST_SERVER == null && System.currentTimeMillis() - time < PING_TIMEOUT) ;
            for (ServerPingManager pinger : PINGERS) {
                if (pinger != null) {
                    pinger.close();
                }
            }
            while (!BEST_SERVER.closed);
            if (BEST_SERVER != null) {
                List<String> ports = servers.get(BEST_SERVER.ip);
                return BEST_SERVER.ip + ":" + ports.get(new Random().nextInt(ports.size()));
            }
        }
        return null;
    }
}
