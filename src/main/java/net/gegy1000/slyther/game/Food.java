package net.gegy1000.slyther.game;

public class Food {
    private int id;
    private short x;
    private short y;
    private byte color;
    private byte size;

    public Food(int id, short x, short y, byte size, byte color) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color;
    }

    public int getId() {
        return this.id;
    }

    public short getX() {
        return this.x;
    }

    public short getY() {
        return this.y;
    }

    public byte getSize() {
        return this.size;
    }

    public byte getColor() {
        return this.color;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Integer) {
            return this.id == (Integer) object;
        } else if (object instanceof Short) {
            return this.id == (Short) object;
        } else if (object instanceof Food) {
            return this.id == ((Food) object).id;
        }
        return false;
    }
}
