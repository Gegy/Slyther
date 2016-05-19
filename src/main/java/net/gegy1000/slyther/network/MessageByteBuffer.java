package net.gegy1000.slyther.network;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MessageByteBuffer {
    private byte[] bytes;
    private int index;
    private int length;

    public static final ByteOrder BYTE_ORDER = ByteOrder.BIG_ENDIAN;

    public MessageByteBuffer() {
        this.bytes = new byte[16384];
        resetIndex();
    }

    public MessageByteBuffer(byte[] bytes) {
        this.resetIndex();
        this.bytes = new byte[bytes.length];
        this.length = bytes.length;
        System.arraycopy(bytes, 0, this.bytes, 0, bytes.length);
    }

    public void writeByte(byte b) {
        bytes[index] = b;
        incrementIndex(1);
    }

    public void writeBytes(byte[] b) {
        for (byte by : b) {
            writeByte(by);
        }
    }

    public void writeInteger(int i) {
        writeBytes(ByteBuffer.allocate(4).order(BYTE_ORDER).putInt(i).array());
    }

    public void writeFloat(float f) {
        writeBytes(ByteBuffer.allocate(4).order(BYTE_ORDER).putFloat(f).array());
    }

    public void writeDouble(double d) {
        writeBytes(ByteBuffer.allocate(8).order(BYTE_ORDER).putDouble(d).array());
    }

    public void writeShort(short s) {
        writeBytes(ByteBuffer.allocate(2).order(BYTE_ORDER).putShort(s).array());
    }

    public void writeInt24(int i) {
        writeBytes(ByteBuffer.allocate(3).order(BYTE_ORDER).putInt(i).array());
    }

    public byte readByte() {
        byte b = bytes[index];
        incrementIndex(1);
        return b;
    }

    public byte[] readBytes(int count) {
        byte[] bytes = new byte[count];

        for (int i = 0; i < count; i++) {
            bytes[i] = readByte();
        }

        return bytes;
    }

    public void writeNullStr16(String str) {
        writeEndStr16(str);
        writeShort((short) 0);
    }

    public void writeEndStr16(String str) {
        for (char c : str.toCharArray()) {
            writeShort((short) c);
        }
    }

    public void writeNullStr8(String str) {
        writeEndStr8(str);
        writeByte((byte) 0);
    }

    public void writeEndStr8(String str) {
        for (char c : str.toCharArray()) {
            writeByte((byte) c);
        }
    }

    public int readInteger() {
        return ByteBuffer.wrap(readBytes(4)).order(BYTE_ORDER).getInt();
    }

    public float readFloat() {
        return ByteBuffer.wrap(readBytes(4)).order(BYTE_ORDER).getFloat();
    }

    public double readDouble() {
        return ByteBuffer.wrap(readBytes(8)).order(BYTE_ORDER).getDouble();
    }

    public short readShort() {
        return ByteBuffer.wrap(readBytes(2)).order(BYTE_ORDER).getShort();
    }

    public int readInt24() {
        byte[] bytes = readBytes(3);
        return ByteBuffer.wrap(new byte[] { 0, bytes[0], bytes[1], bytes[2] }).order(BYTE_ORDER).getInt();
    }

    public String readNullStr16() {
        String str = "";

        short c;

        while ((c = readShort()) != 0) {
            str += (char) c;
        }

        return str;
    }

    public String readEndStr16() {
        String str = "";

        while (hasNext(2)) {
            str += (char) readShort();
        }

        return str;
    }

    public String readNullStr8() {
        String str = "";

        byte c;

        while ((c = readByte()) != 0) {
            str += (char) c;
        }

        return str;
    }

    public String readEndStr8() {
        String str = "";

        while (hasNext(1)) {
            str += (char) readByte();
        }

        return str;
    }

    public void resetIndex() {
        index = 0;
    }

    public void incrementIndex(int amount) {
        index += amount;

        if (index > length) {
            length = index;
        }
    }

    public byte[] toBytes() {
        byte[] returnBytes = new byte[length];

        System.arraycopy(bytes, 0, returnBytes, 0, length);

        return returnBytes;
    }

    public boolean hasNext() {
        return hasNext(1);
    }

    public boolean hasNext(int count) {
        return index + count <= length;
    }

    public int getBytesLeft() {
        return bytes.length - index;
    }

    public int length() {
        return length;
    }

    public int getIndex() {
        return index;
    }

    public int getLength() {
        return length;
    }
}