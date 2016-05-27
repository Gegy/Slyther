package net.gegy1000.slyther.client;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class GameReplayer {
    private FileChannel channel;
    private ClientNetworkManager networkManager;
    private long lastTime;
    private ReplayPacket next;
    private long position;

    public GameReplayer(File file, ClientNetworkManager networkManager) throws IOException {
        channel = new RandomAccessFile(file, "r").getChannel();
        this.networkManager = networkManager;
        next = getNextPacket();
    }

    public boolean tick() throws IOException {
        long time = System.currentTimeMillis();
        boolean shouldContinue = true;
        if (next != null) {
            while (shouldContinue) {
                if (time - lastTime >= next.timeDelta) {
                    next.apply();
                    next = getNextPacket();
                    if (next == null || position >= channel.size()) {
                        return false;
                    }
                    lastTime = time;
                } else {
                    shouldContinue = false;
                }
            }
        }
        return next != null;
    }

    public ReplayPacket getNextPacket() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        read(buffer);
        short timeDelta = buffer.getShort();
        buffer = ByteBuffer.allocate(2);
        read(buffer);
        short packetLength = buffer.getShort();
        buffer = ByteBuffer.allocate(packetLength);
        read(buffer);
        return new ReplayPacket(timeDelta, buffer);
    }

    private void read(ByteBuffer buffer) throws IOException {
        channel.position(position);
        channel.read(buffer);
        position += buffer.limit();
        buffer.position(0);
    }

    public class ReplayPacket {
        private short timeDelta;
        private ByteBuffer data;

        public ReplayPacket(short timeDelta, ByteBuffer data) {
            this.timeDelta = timeDelta;
            this.data = data;
        }

        public void apply() {
            networkManager.onMessage(data);
        }
    }
}
