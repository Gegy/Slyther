package net.gegy1000.slyther.client.game;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Color;

public class Food {
    public SlytherClient client;
    public int id;
    public float posX;
    public float posY;
    public float renderX;
    public float renderY;
    public int rsp;
    public Color color;
    public float rad;
    public float size;
    public float lrrad;
    public float fr;
    public float gfr;
    public float gr;
    public float wsp;
    public float eatenFr;
    public boolean eaten;
    public Snake eater;
    public int sectorX;
    public int sectorY;

    public Food(SlytherClient client, int id, float x, float y, float size, boolean isNatural, Color color) {
        this.client = client;
        this.id = id;
        posX = x;
        posY = y;
        renderX = x;
        renderY = y;
        rsp = isNatural ? 2 : 1;
        this.color = color;
        rad = 0.00001F; //rendering with rad not size?
        this.size = size;
        lrrad = rad;
        gfr = (int) (64.0F * Math.random());
        gr = 0.64F + 0.1F * this.size;
        wsp = (float) (0.0225F * (2.0F * Math.random() - 1.0F));
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Food && id == ((Food) object).id;
    }
}
