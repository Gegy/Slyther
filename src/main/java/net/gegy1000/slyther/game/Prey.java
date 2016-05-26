package net.gegy1000.slyther.game;

import net.gegy1000.slyther.client.SlytherClient;

public class Prey {
    public SlytherClient client;

    public int id;
    public float posX;
    public float posY;
    public float rad;
    public float sz;
    public Color cv;
    public int dir;
    public float wang;
    public float ang;
    public float sp;
    public float fr;
    public int gfr;
    public int rr;
    public int gg;
    public int bb;
    public int cs;
    public float[] fxs;
    public float[] fys;
    public int fpos;
    public int ftg;
    public float fx;
    public float fy;
    public boolean eaten;
    public float eatenFR;
    public Snake eater;
    public float gr;

    public Prey(SlytherClient client, int id, float x, float y, float size, Color cv, int dir, float wang, float ang, float sp) {
        this.client = client;
        this.id = id;
        this.posX = x;
        this.posY = y;
        this.rad = 0.00001F;
        this.sz = size;
        this.cv = cv;
        this.dir = dir;
        this.wang = wang;
        this.ang = ang;
        this.sp = sp;
        this.gfr = (int) (64 * Math.random());
        SkinColor color = SkinColor.values()[this.cv.ordinal()];
        this.rr = (int) Math.min(255, color.red + Math.floor(20.0F * Math.random()));
        this.gg = (int) Math.min(255, color.green + Math.floor(20.0F * Math.random()));
        this.bb = (int) Math.min(255, color.blue + Math.floor(20.0F * Math.random()));
        this.cs = ((rr & 0xFF) << 16) | ((gg & 0xFF) << 8) | (bb & 0xFF);
        this.fxs = new float[SlytherClient.RFC];
        this.fys = new float[SlytherClient.RFC];
    }

    public void update(float vfr, float vfrb) {
        float c = client.MAMU2 * vfr;
        float movement = this.sp * vfr / 4;
        if (vfrb > 0) {
            if (this.ftg > 0) {
                float h = vfrb;
                if (h > this.ftg) {
                    h = this.ftg;
                }
                this.ftg -= h;
                for (int qq = 1; qq <= h; qq++) {
                    if (qq == h) {
                        this.fx = this.fxs[this.fpos];
                        this.fy = this.fys[this.fpos];
                    }
                    this.fxs[this.fpos] = 0;
                    this.fys[this.fpos] = 0;
                    this.fpos++;
                    if (this.fpos >= SlytherClient.RFC) {
                        this.fpos = 0;
                    }
                }
            } else if (this.ftg == 0) {
                this.fx = 0;
                this.fy = 0;
                this.ftg = -1;
            }
        }
        if (this.dir == 1) {
            this.ang -= c;
            if (this.ang < 0 || this.ang >= SlytherClient.PI_2) {
                this.ang %= SlytherClient.PI_2;
            }
            if (this.ang < 0) {
                this.ang += SlytherClient.PI_2;
            }
            float h = (float) ((this.wang -= this.ang) % SlytherClient.PI_2);
            if (h < 0) {
                h += SlytherClient.PI_2;
            }
            if (h > Math.PI) {
                h -= SlytherClient.PI_2;
            }
            if (h > 0) {
                this.ang = this.wang;
                this.dir = 0;
            }
        } else if (this.dir == 2) {
            this.ang += c;
            if (this.ang < 0 || this.ang >= SlytherClient.PI_2) {
                this.ang %= SlytherClient.PI_2;
            }
            if (this.ang < 0) {
                this.ang += SlytherClient.PI_2;
            }
            float h = (float) ((this.wang -= this.ang) % SlytherClient.PI_2);
            if (h < 0) {
                h += SlytherClient.PI_2;
            }
            if (h > Math.PI) {
                h -= SlytherClient.PI_2;
            }
            if (h < 0) {
                this.ang = this.wang;
                this.dir = 0;
            }
        } else {
            this.ang = this.wang;
        }
        this.posX += Math.cos(this.ang) * movement;
        this.posY += Math.sin(this.ang) * movement;
        this.gfr += vfr * this.gr;
        if (this.eaten) {
            if (this.fr != 1.5F) {
                this.fr += vfr / 150.0F;
                if (this.fr >= 1.5F) {
                    this.fr = 1.5F;
                }
            }
            this.eatenFR += vfr / 47.0F;
            this.gfr += vfr;
            if (this.eatenFR >= 1 || eater == null) {
                client.preys.remove(this);
            } else {
                this.rad = (float) (1 - Math.pow(this.eatenFR, 3));
            }
        } else {
            if (this.fr != 1.0F) {
                this.fr += vfr / 150.0F;
                if (this.fr >= 1.0F) {
                    this.fr = 1.0F;
                    this.rad = 1.0F;
                } else {
                    this.rad = (float) (0.5F * (1.0F - Math.cos(Math.PI * this.fr)));
                    this.rad += 0.66F * (0.5F * (1.0F - Math.cos(Math.PI * this.rad)) - this.rad);
                }
            }
        }
    }
}
