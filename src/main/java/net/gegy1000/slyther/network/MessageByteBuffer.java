package net.gegy1000.slyther.network;

import java.nio.ByteBuffer;

import org.apache.commons.io.Charsets;

public class MessageByteBuffer {
    private ByteBuffer buf;

    private byte[] work = new byte[8];

    public MessageByteBuffer() {
        buf = ByteBuffer.allocate(16384);
    }

    public MessageByteBuffer(byte[] array) {
        buf = ByteBuffer.wrap(array);
    }

    public MessageByteBuffer(ByteBuffer buf) {
        if (!buf.hasArray()) {
            throw new IllegalArgumentException("Buffer must be backed by an array");
        }
        this.buf = buf;
    }

    public void write(byte b) {
        buf.put(b);
    }

    public void write(byte[] src) {
        buf.put(src);
    }

    public void writeInt(int value) {
        buf.putInt(value);
    }

    public void writeShort(int value) {
        buf.putShort((short) (value & 0xFFFF));
    }

    public void writeInt24(int value) {
        work[0] = (byte) (value >> 16 & 0xFF);
        work[1] = (byte) (value >> 8 & 0xFF);
        work[2] = (byte) (value & 0xFF);
        buf.put(work, 0, 3);
    }

    public void writeASCIIBytes(String str) {
        buf.put(str.getBytes(Charsets.US_ASCII));
    }

    public int read() {
        return buf.get() & 0xFF;
    }

    public byte[] read(int count) {
        byte[] dst = new byte[count];
        buf.get(dst);
        return dst;
    }

    public int readInt() {
        return buf.getInt();
    }

    public int readShort() {
        return buf.getShort() & 0xFFFF;
    }

    public int readInt24() {
        buf.get(work, 0, 3);
        return (work[0] & 0xFF) << 16 | (work[1] & 0xFF) << 8 | (work[2] & 0xFF);
    }

    public void skipBytes(int n) {
        buf.position(buf.position() + n);
    }

    public byte[] array() {
        byte[] array = new byte[buf.position()];
        System.arraycopy(buf.array(), buf.arrayOffset(), array, 0, array.length);
        return array;
    }

    public boolean hasRemaining() {
        return buf.hasRemaining();
    }

    public boolean hasRemaining(int n) {
        return buf.position() + n <= buf.limit();
    }

    public int remaining() {
        return buf.remaining();
    }

    public int limit() {
        return buf.limit();
    }

    public int position() {
        return buf.position();
    }
}