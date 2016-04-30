package net.gegy1000.slyther.game;

public class SnakePart {
    private int diffX;
    private int diffY;

    public SnakePart(int diffX, int diffY) {
        this.diffX = diffX;
        this.diffY = diffY;
    }

    public int getDiffX() {
        return this.diffX;
    }

    public int getDiffY() {
        return this.diffY;
    }
}
