package net.gegy1000.slyther.network;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import net.gegy1000.slyther.util.Log;

public class ServerPingerDispatcher implements Runnable {
    private static final int PING_TIMEOUT = 20000;

    private static final int MIN_FOR_PLAY = 5;

    private static final int MIN_FOR_PING = 10;

    private static final Pinger POISON = new Pinger() {};

    @Override
    public void run() {
        ServerHandler.INSTANCE.serversAvailable.lock();
        List<ServerHandler.Server> servers = ServerHandler.INSTANCE.getServerList();
        List<ServerPinger> pingers = new ArrayList<>();
        BlockingQueue<Pinger> finishedPingers = new LinkedBlockingQueue<>();
        if (servers.size() > 0) {
            for (ServerHandler.Server server : servers) {
                try {
                    ServerPinger pinger = new ServerPinger(server, finishedPingers);
                    pinger.connect();
                    pingers.add(pinger);
                } catch (URISyntaxException e) {
                    Log.catching(e);
                }
            }
            int pinged = 0;
            Thread timeout = new Thread(() -> {
                try {
                    Thread.sleep(PING_TIMEOUT);
                    finishedPingers.add(POISON);
                } catch (InterruptedException e) {}
            }, "ClusterPingerDispatcherTimeout");
            timeout.start();
            try {
                Pinger pinger;
                while ((pinger = finishedPingers.take()) != POISON) {
                    pingers.remove(pinger);
                    pinged++;
                    if (pinged == MIN_FOR_PLAY) {
                        ServerHandler.INSTANCE.serversAvailable.unlock();
                    } else if (pinged == MIN_FOR_PING) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Log.catching(e);
            } finally {
                pingers.forEach(p -> p.closeConnection(1006, ""));
                finishedPingers.clear();
                if (pinged < MIN_FOR_PLAY) {
                    ServerHandler.INSTANCE.serversAvailable.unlock();   
                }
                if (timeout.isAlive()) {
                    timeout.interrupt();
                }
            }
        }
    }

    private interface Pinger {}

    private class ServerPinger extends WebSocketClient implements Pinger {
        private ServerHandler.Server server;

        private BlockingQueue<Pinger> finishedPingers;

        private int pingCount = 0;

        private int[] pings = new int[4];

        private long pingSendTime;

        public ServerPinger(ServerHandler.Server server, BlockingQueue<Pinger> finishedPingers) throws URISyntaxException {
            super(new URI("ws://" + server.getClusterIp() + ":80/ptc"), new Draft_17(), ServerHandler.INSTANCE.getHeaders(), 0);
            this.server = server;
            this.finishedPingers = finishedPingers;
        }

        @Override
        public void run() {
            Thread.currentThread().setName("Pinger(" + getURI() + ")");
            super.run();
        }

        @Override
        public void onOpen(ServerHandshake handshake) {
            pingSendTime = System.currentTimeMillis();
            send("p");
        }

        @Override
        public void onMessage(ByteBuffer buffer) {
            if (buffer.get() == 'p') {
                if (pingCount < 4) {
                    long currentTime = System.currentTimeMillis();
                    pings[pingCount++] = (int) (currentTime - pingSendTime);
                    pingSendTime = currentTime;
                    send("p");
                } else {
                    server.setPing(pings);
                    Log.debug("Ping time of {}", server.getPing());
                    close();
                    finishedPingers.add(this);
                }
            }
        }

        @Override
        public void onMessage(String message) {}

        @Override
        public void onClose(int code, String reason, boolean remote) {}

        @Override
        public void onError(Exception ex) {}
    }
}