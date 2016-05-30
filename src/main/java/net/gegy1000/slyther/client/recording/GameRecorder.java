package net.gegy1000.slyther.client.recording;

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

import net.gegy1000.slyther.util.UIUtils;

import org.apache.commons.io.IOUtils;

public class GameRecorder extends Thread implements Closeable {
    private static final TimedMessage POISON = new TimedMessage();

    private Thread thread;

    private File file;

    private FileOutputStream fout;

    private DataOutput dout;

    private BlockingQueue<TimedMessage> messages = new LinkedBlockingQueue<>();

    private Queue<TimedMessage> msgPool = new ConcurrentLinkedQueue<>();

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
            UIUtils.displayException("Unable to open recording output file", e);
            return;
        }
        dout = new DataOutputStream(fout);
        lastTime = System.currentTimeMillis();
        try {
            TimedMessage msg;
            while ((msg = messages.take()) != POISON) {
                dout.writeShort(msg.timeSinceLastMessage);
                dout.writeShort(msg.payload.length);
                dout.write(msg.payload);
                msgPool.add(msg);
            }
        } catch (Exception e) {
            UIUtils.displayException("A problem occured while recording", e);
        } finally {
            IOUtils.closeQuietly(fout);
            msgPool.clear();
        }
    }

    public void onMessage(byte[] payload) {
        TimedMessage msg = msgPool.isEmpty() ? new TimedMessage() : msgPool.poll();
        long time = System.currentTimeMillis();
        msg.timeSinceLastMessage = (short) (time - lastTime);
        msg.payload = payload;
        lastTime = time;
        messages.add(msg);
    }

    @Override
    public void close() {
        messages.add(POISON);
    }
}
