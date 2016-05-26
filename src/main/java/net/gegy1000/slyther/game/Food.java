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
        this.posX = x;
        this.posY = y;
        this.rx = x;
        this.ry = y;
        this.rsp = u ? 2 : 1;
        this.cv = color;
        this.rad = 0.00001F; //rendering with rad not sz?
        this.sz = size;
        this.lrrad = this.rad;
        this.gfr = (int) (64.0F * Math.random());
        this.gr = 0.64F + 0.1F * this.sz;
        this.wsp = (float) (0.0225F * (2.0F * Math.random() - 1.0F));
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Food && this.id == ((Food) object).id;
    }
}
