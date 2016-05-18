package net.gegy1000.slyther.game;

public class Food {
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
    public float fw;
    public float fh;

    public Food(int id, float x, float y, float size, boolean u, Color color) {
        this.id = id;
        this.posX = x;
        this.posY = y;
        this.rx = x;
        this.ry = y;
        this.rsp = u ? 2 : 1;
        this.cv = color;
        this.rad = 0.00001F;
        this.sz = size;
        this.lrrad = this.rad;
        this.gfr = (int) (64.0F * Math.random());
        this.gr = 0.64F + 0.1F * this.sz;
        this.wsp = (float) (0.0225F * (2.0F * Math.random() - 1.0F));
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
