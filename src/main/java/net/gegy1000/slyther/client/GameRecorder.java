package net.gegy1000.slyther.client;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class GameRecorder {
    private FileChannel channel;
    private long lastTime;
    private long position;

    public GameRecorder(File file) throws IOException {
        this.channel = new RandomAccessFile(file, "rw").getChannel();
    }

    public void onMessage(byte[] messageBuffer) throws IOException {
        long time = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort((short) (time - lastTime));
        this.write(buffer.array());
        buffer = ByteBuffer.allocate(2);
        buffer.putShort((short) messageBuffer.length);
        this.write(buffer.array());
        this.write(messageBuffer);
        lastTime = time;
    }

    private void write(byte[] bytes) throws IOException {
        this.channel.position(position);
        this.channel.write(ByteBuffer.wrap(bytes));
        this.position += bytes.length;
    }

    public void close() {
        try {
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
