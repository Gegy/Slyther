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
        posX = x;
        posY = y;
        rad = 0.00001F;
        sz = size;
        this.cv = cv;
        this.dir = dir;
        this.wang = wang;
        this.ang = ang;
        this.sp = sp;
        gfr = (int) (64 * Math.random());
        SkinColor color = SkinColor.values()[this.cv.ordinal()];
        rr = (int) Math.min(255, color.red + Math.floor(20.0F * Math.random()));
        gg = (int) Math.min(255, color.green + Math.floor(20.0F * Math.random()));
        bb = (int) Math.min(255, color.blue + Math.floor(20.0F * Math.random()));
        cs = ((rr & 0xFF) << 16) | ((gg & 0xFF) << 8) | (bb & 0xFF);
        fxs = new float[SlytherClient.RFC];
        fys = new float[SlytherClient.RFC];
    }

    public void update(float vfr, float vfrb) {
        float turnSpeed = client.MAMU2 * vfr;
        float moveAmount = sp * vfr / 4;
        if (vfrb > 0) {
            if (ftg > 0) {
                float h = vfrb;
                if (h > ftg) {
                    h = ftg;
                }
                ftg -= h;
                for (int qq = 1; qq <= h; qq++) {
                    if (qq == h) {
                        fx = fxs[fpos];
                        fy = fys[fpos];
                    }
                    fxs[fpos] = 0;
                    fys[fpos] = 0;
                    fpos++;
                    if (fpos >= SlytherClient.RFC) {
                        fpos = 0;
                    }
                }
            } else if (ftg == 0) {
                fx = 0;
                fy = 0;
                ftg = -1;
            }
        }
        if (dir == 1) {
            ang -= turnSpeed;
            if (ang < 0 || ang >= SlytherClient.PI_2) {
                ang %= SlytherClient.PI_2;
            }
            if (ang < 0) {
                ang += SlytherClient.PI_2;
            }
            float h = (float) ((wang - ang) % SlytherClient.PI_2);
            if (h < 0) {
                h += SlytherClient.PI_2;
            }
            if (h > Math.PI) {
                h -= SlytherClient.PI_2;
            }
            if (h > 0) {
                ang = wang;
                dir = 0;
            }
        } else if (dir == 2) {
            ang += turnSpeed;
            if (ang < 0 || ang >= SlytherClient.PI_2) {
                ang %= SlytherClient.PI_2;
            }
            if (ang < 0) {
                ang += SlytherClient.PI_2;
            }
            float h = (float) ((wang - ang) % SlytherClient.PI_2);
            if (h < 0) {
                h += SlytherClient.PI_2;
            }
            if (h > Math.PI) {
                h -= SlytherClient.PI_2;
            }
            if (h < 0) {
                ang = wang;
                dir = 0;
            }
        } else {
            ang = wang;
        }
        posX += Math.cos(ang) * moveAmount;
        posY += Math.sin(ang) * moveAmount;
        gfr += vfr * gr;
        if (eaten) {
            if (fr != 1.5F) {
                fr += vfr / 150.0F;
                if (fr >= 1.5F) {
                    fr = 1.5F;
                }
            }
            eatenFR += vfr / 47.0F;
            gfr += vfr;
            if (eatenFR >= 1 || eater == null) {
                client.preys.remove(this);
            } else {
                rad = (float) (1 - Math.pow(eatenFR, 3));
            }
        } else {
            if (fr != 1.0F) {
                fr += vfr / 150.0F;
                if (fr >= 1.0F) {
                    fr = 1.0F;
                    rad = 1.0F;
                } else {
                    rad = (float) (0.5F * (1.0F - Math.cos(Math.PI * fr)));
                    rad += 0.66F * (0.5F * (1.0F - Math.cos(Math.PI * rad)) - rad);
                }
            }
        }
    }
}
