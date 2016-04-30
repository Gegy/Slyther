package net.gegy1000.slyther.game;

import java.util.List;

public class Snake {
    private short id;
    private int x;
    private int y;
    private String name;
    private Skin skin;
    private List<SnakePart> parts;

    public Snake(String name, Skin skin, short id, int x, int y, List<SnakePart> parts) {
        this.name = name;
        this.skin = skin;
        this.id = id;
        this.x = x;
        this.y = y;
        this.parts = parts;
    }

    public short getId() {
        return this.id;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public String getName() {
        return this.name;
    }

    public Skin getSkin() {
        return this.skin;
    }

    public List<SnakePart> getParts() {
        return this.parts;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Integer) {
            return this.id == (Integer) object;
        } else if (object instanceof Short) {
            return this.id == (Short) object;
        } else if (object instanceof Snake) {
            return this.id == ((Snake) object).id;
        }
        return false;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
