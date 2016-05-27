package net.gegy1000.slyther.game;

import net.gegy1000.slyther.client.SlytherClient;

public class Food {
    public SlytherClient client;
    public int id;
    public float posX;
    public float posY;
    public float rx;
    public float ry;
    public int rsp;
    public Color cv;
    public float rad;
    public float sz;
    public float lrrad;
    public float fr;
    public float gfr;
    public float gr;
    public float wsp;
    public float eatenFr;
    public boolean eaten;
    public Snake eater;
    public int sx; // Sector X?
    public int sy;

    public Food(SlytherClient client, int id, float x, float y, float size, boolean u, Color color) {
        this.client = client;
        this.id = id;
        posX = x;
        posY = y;
        rx = x;
        ry = y;
        rsp = u ? 2 : 1;
        cv = color;
        rad = 0.00001F; //rendering with rad not sz?
        sz = size;
        lrrad = rad;
        gfr = (int) (64.0F * Math.random());
        gr = 0.64F + 0.1F * sz;
        wsp = (float) (0.0225F * (2.0F * Math.random() - 1.0F));
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Food && id == ((Food) object).id;
    }
}
