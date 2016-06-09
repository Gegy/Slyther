package net.gegy1000.slyther.client.recording;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.Thread.State;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import net.gegy1000.slyther.util.Log;
import net.gegy1000.slyther.util.UIUtils;

import org.apache.commons.io.IOUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class GameReplayer implements Runnable {
    private WebSocketServer server;

    private File file;

    private Thread thread;

    private FileInputStream fin;

    private DataInputStream din;

    private ByteBuffer messageBuffer = ByteBuffer.allocate(1024);

    private boolean waitingForOpen = true;

    private boolean waitingForClose;

    public GameReplayer(File file) {
        this.file = file;
        server = new WebSocketServer(new InetSocketAddress(8004)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                if (waitingForOpen) {
                    thread = new Thread(GameReplayer.this, "Replayer");
                    thread.start();   
                    waitingForOpen = false;
                } else {
                    Log.warn("Connection was attempted to be made during playback: {}", conn.getRemoteSocketAddress());
                    conn.close();   
                }
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                waitingForClose = true;
                // Interrupt a long message delay
                while (waitingForClose) {
                    if (thread.getState() == State.TIMED_WAITING) {
                        thread.interrupt();
                    }
                }
            }

            @Override
            public void onMessage(WebSocket conn, String message) {}

            @Override
            public void onError(WebSocket conn, Exception ex) {
                if (ex instanceof BindException) {
                    Log.catching(ex);
                }
            }
        };
        server.start();
    }

    public URI getURI() {
        try {
            InetSocketAddress addr = server.getAddress();
            return new URI("ws://" + addr.getHostString() + ":" + addr.getPort());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            fin = new FileInputStream(file);
        } catch (IOException e) {
            UIUtils.displayException("Unable to open recording input file", e);
            return;
        }
        din = new DataInputStream(fin);
        long lastTime = System.currentTimeMillis();
        try {
            while (!waitingForClose) {
                int delta = din.readShort() & 0xFFFF;
                int length = din.readShort() & 0xFFFF;
                if (length > messageBuffer.capacity()) {
                    messageBuffer = ByteBuffer.allocate(length);
                }
                messageBuffer.rewind();
                messageBuffer.limit(length);
                byte[] payload = messageBuffer.array();
                din.read(payload, 0, length);
                long delay = System.currentTimeMillis() - (lastTime + delta);
                if (delay > 100) {
                    Thread.sleep(delay - 100);
                }
                while (System.currentTimeMillis() - (lastTime + delta) < 0);
                lastTime = System.currentTimeMillis();
                server.connections().forEach(conn -> conn.send(messageBuffer));
            }
        } catch (EOFException | InterruptedException e) {
            // Graceful finish
        } catch (Exception e) {
            UIUtils.displayException("A problem occured while playing back recording", e);
        } finally {
            waitingForClose = false;
            IOUtils.closeQuietly(fin);
            try {
                server.stop();
            } catch (IOException | InterruptedException e) {
                Log.error("Exception while stopping server");
                Log.catching(e);
            }
        }
    }
}
