package net.gegy1000.slyther.client;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.IOUtils;

public class GameRecorder extends Thread implements Closeable {
    private static final Msg POISON = new Msg();

    private Thread thread;

    private File file;

    private FileOutputStream fout;

    private DataOutput dout;

    private BlockingQueue<Msg> messages = new LinkedBlockingQueue<>();

    private Queue<Msg> msgPool = new ConcurrentLinkedQueue<>();

    private long lastTime;

    public GameRecorder(File file) {
        this.file = file;
    }

    @Override
    public synchronized void start() {
        if (thread == null) {
            thread = new Thread(this, "Recorder");
            thread.start();
        }
    }

    @Override
    public void run() {
        try {
            fout = new FileOutputStream(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dout = new DataOutputStream(fout);
        try {
            Msg msg;
            while ((msg = messages.take()) != POISON) {
                dout.writeShort(msg.timeSinceLastMessage);
                dout.writeShort(msg.payload.length);
                dout.write(msg.payload);
                msgPool.add(msg);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fout);
        }
    }

    public void onMessage(byte[] payload) {
        Msg msg = msgPool.isEmpty() ? new Msg() : msgPool.poll();
        long time = System.currentTimeMillis();
        msg.timeSinceLastMessage = (short) (time - lastTime);
        msg.payload = payload;
        lastTime = time;
        messages.add(msg);
    }

    @Override
    public void close() {
        messages.add(POISON);
        msgPool.clear();
    }

    private static class Msg {
        short timeSinceLastMessage;
        byte[] payload;
    }
}
