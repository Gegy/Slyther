package net.gegy1000.slyther.game;

import net.gegy1000.slyther.client.SlytherClient;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class Snake {
    public SlytherClient client;

    public String name;
    public int id;
    public float posX;
    public float posY;
    public Skin rcv;
    public int er;
    public float pr;
    public float pma;
    public int ec;
    public float eca;
    public int ppa;
    public int ppc;
    public boolean antenna;
    public boolean oneEye;
    public float swell;
    public float atba;
    public int atc1;
    public int atc2;
    public boolean atwg;
    public float atia;
    public boolean abrot;
    public float[] atx;
    public float[] aty;
    public float[] atvx;
    public float[] atvy;
    public float[] atax;
    public float[] atay;
    public int blbx;
    public int blby;
    public int blbw;
    public int blbh;
    public float bsc;
    public float blba;
    public int ebi;
    public int ebiw;
    public int ebih;
    public int ebisz;
    public int epi;
    public int epiw;
    public int epih;
    public int episz;
    public SkinColor[] rbcs;
    public SkinDetails skinDetails;
    public SkinColor cv; // color value
    public int fnfr = 0;
    public int na;
    public float chl;
    public float tsp;
    public int sfr;
    public int rr;
    public int gg;
    public int bb;
    public int cs;
    public int cs04;
    public int csw;
    public float sc;
    public float ssp;
    public float fsp; // Fast speed?
    public float msp;
    public float[] fxs;
    public float[] fys;
    public float[] fchls;
    public int fpos;
    public int ftg;
    public float fx;
    public float fy;
    public float fchl;
    public float[] fas;
    public int fapos;
    public int fatg;
    public float fa;
    public float ehang;
    public float wehang;
    public int ehl;
    public int msl;
    public double fam;
    public float ang;
    public float eang;
    public float wang;
    public float rex;
    public float rey;
    public float sp;
    public SnakePoint lnp; // Tail point or Head point (Last point entry)
    public List<SnakePoint> pts;
    public int sct;
    public int flpos;
    public float[] fls;
    public float fl;
    public int fltg;
    public double tl;
    public double cfl;
    public float scang;
    public float spang;
    public float deadAmt;
    public float aliveAmt;
    public boolean md;
    public boolean prevMd;
    public boolean dead;
    public int dir;
    public int edir;
    public float sep;
    public float wsep;
    public boolean iiv;
    public boolean antennaShown;
    public String antennaTexture;

    public Snake(SlytherClient client, int id, float x, float y, Skin skin, float angle, List<SnakePoint> points) {
        this.client = client;
        this.id = id;
        posX = x;
        posY = y;
        setSkin(skin);
        na = 1;
        rr = (int) Math.min(255, cv.red + Math.floor(20.0F * Math.random()));
        gg = (int) Math.min(255, cv.green + Math.floor(20.0F * Math.random()));
        bb = (int) Math.min(255, cv.blue + Math.floor(20.0F * Math.random()));
        cs = ((rr & 0xFF) << 16) | ((gg & 0xFF) << 8) | (bb & 0xFF);
        cs04 = (int) ((Math.min(255, Math.max(0, Math.round(0.4 * rr))) & 0xFF) << 16 | Math.min(255, Math.max(0, Math.round(0.4 * gg) & 0xFF) << 8) | Math.min(255, Math.max(0, Math.round(0.4 * bb) & 0xFF)));
        csw = (int) ((Math.min(255, Math.max(0, Math.round(0.5 * rr))) & 0xFF) << 16 | Math.min(255, Math.max(0, Math.round(0.5 * gg) & 0xFF) << 8) | Math.min(255, Math.max(0, Math.round(0.5 * bb) & 0xFF)));
        sc = 1.0F;
        ssp = client.NSP1 + client.NSP2 * sc;
        fsp = ssp + 0.1F;
        msp = client.NSP3;
        fxs = new float[SlytherClient.RFC];
        fys = new float[SlytherClient.RFC];
        fchls = new float[SlytherClient.RFC];
        fas = new float[SlytherClient.AFC];
        ehang = angle;
        wehang = angle;
        ehl = 1;
        msl = 42;
        ang = angle;
        eang = angle;
        wang = angle;
        sp = 2;

        if (points != null) {
            lnp = points.get(points.size() - 1);
            pts = points;
            sct = points.size();
            if (points.get(0).dying) {
                sct--;
            }
        } else {
            pts = new ArrayList<>();
        }

        fls = new float[SlytherClient.LFC];
        tl = sct + fam;
        cfl = tl;
        scang = 1;
        deadAmt = 0;
        aliveAmt = 0;
    }

    public void setSkin(Skin skin) {
        rcv = skin;
        er = 6;
        pr = 3.5F;
        pma = 2.3F;
        ec = 0xFFFFFF;
        eca = 0.75F;
        ppa = 1;

        SkinDetails details = SkinHandler.INSTANCE.getDetails(skin);

        SkinColor[] pattern = new SkinColor[] { SkinColor.values()[skin.ordinal() % SkinColor.values().length] };

        if (details != null) {
            antenna = details.hasAntenna;
            atc1 = details.antennaPrimaryColor;
            atc2 = details.antennaSecondaryColor;
            atwg = details.atwg;
            atia = details.atia;
            abrot = details.abrot;
            int antennaLength = details.antennaLength;
            atx = new float[antennaLength];
            aty = new float[antennaLength];
            atvx = new float[antennaLength];
            atvy = new float[antennaLength];
            atax = new float[antennaLength];
            atay = new float[antennaLength];
            for (int i = 0; i < antennaLength; i++) {
                atx[i] = posX;
                aty[i] = posY;
            }
            blbx = details.blbx;
            blby = details.blby;
            blbw = details.blbw;
            blbh = details.blbh;
            bsc = details.bsc;
            blba = details.blba;
            ec = details.eyeColor;
            eca = details.eca;
            oneEye = details.oneEye;
            ebiw = details.ebiw;
            ebih = details.ebih;
            ebisz = details.ebisz;
            epiw = details.epiw;
            epih = details.epih;
            episz = details.episz;
            pma = details.pma;
            swell = details.swell;
            antennaTexture = details.antennaTexture;

            pattern = details.pattern;
            skinDetails = details;
        }

        rbcs = pattern;
        cv = pattern[0];
    }

    public void snl() {
        double d = tl;
        tl = sct + fam;
        d = tl - d;
        int b = flpos;
        for (int i = 0; i < SlytherClient.LFC; i++) {
            fls[b] -= d * SlytherClient.LFAS[i];
            b++;
            if (b >= SlytherClient.LFC) {
                b = 0;
            }
        }
        fl = fls[flpos];
        fltg = SlytherClient.LFC;
        if (this == client.player) {
            client.wumsts = true;
        }
    }

    public void update(float vfr, float vfrb, float vfrb2) {
        float c = client.MAMU * vfr * scang * spang;
        float moveAmount = sp * vfr / 4;
        if (moveAmount > msl) {
            moveAmount = msl;
        }
        if (client.allowUserInput) {
            if (this == client.player) {
                boolean prev = md;
                md = Mouse.isButtonDown(0) || Mouse.isButtonDown(1);
                if (prev != md) {
                    prevMd = prev;
                }
            }
        }
        if (!dead) {
            if (tsp != sp) {
                if (tsp < sp) {
                    tsp += 0.3F * vfr;
                    if (tsp > sp) {
                        tsp = sp;
                    }
                } else {
                    tsp -= 0.3F * vfr;
                    if (tsp < sp) {
                        tsp = sp;
                    }
                }
            }
            if (tsp > fsp) {
                sfr += (tsp - fsp) * vfr * 0.021F;
            }
            if (fltg > 0) {
                float h = vfrb;
                if (h > fltg) {
                    h = fltg;
                }
                fltg -= h;
                for (int i = 0; i < h; i++) {
                    fl = fls[flpos];
                    fls[flpos] = 0;
                    flpos++;
                    if (flpos >= SlytherClient.LFC) {
                        flpos = 0;
                    }
                }
            } else {
                if (fltg == 0) {
                    fltg = -1;
                    fl = 0;
                }
            }
            cfl = tl + fl;
        }
        if (dir == 1) {
            ang -= c;
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
            ang += c;
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
        if (ehl != 1) {
            ehl += 0.03F * vfr;
            if (ehl >= 1) {
                ehl = 1;
            }
        }
        SnakePoint point = pts.get(pts.size() - 1);
        if (point != null) {
            wehang = (float) Math.atan2(posY + fy - point.posY - point.fy + point.eby * (1.0F - ehl), posX + fx - point.posX - point.fx + point.ebx * (1.0F - ehl));
        }
        if (!dead) {
            if (ehang != wehang) {
                float h = (float) ((wehang - ehang) % SlytherClient.PI_2);
                if (h < 0) {
                    h += SlytherClient.PI_2;
                }
                if (h > Math.PI) {
                    h -= SlytherClient.PI_2;
                }
                if (h < 0) {
                    edir = 1;
                } else {
                    if (h > 0) {
                        edir = 2;
                    }
                }
            }
        }
        if (edir == 1) {
            ehang -= 0.1F * vfr;
            if (ehang < 0 || ehang >= SlytherClient.PI_2) {
                ehang %= SlytherClient.PI_2;
            }
            if (ehang < 0) {
                ehang += SlytherClient.PI_2;
            }
            float h = (float) ((wehang - ehang) % SlytherClient.PI_2);
            if (h < 0) {
                h += SlytherClient.PI_2;
            }
            if (h > Math.PI) {
                h -= SlytherClient.PI_2;
            }
            if (h > 0) {
                ehang = wehang;
                edir = 0;
            }
        } else if (edir == 2) {
            ehang += 0.1F * vfr;
            if (ehang < 0 || ehang >= SlytherClient.PI_2) {
                ehang %= SlytherClient.PI_2;
            }
            if (ehang < 0) {
                ehang += SlytherClient.PI_2;
            }
            float h = (float) ((wehang - ehang) % SlytherClient.PI_2);
            if (h < 0) {
                h += SlytherClient.PI_2;
            }
            if (h > Math.PI) {
                h -= SlytherClient.PI_2;
            }
            if (h < 0) {
                ehang = wehang;
                edir = 0;
            }
        }
        if (!dead) {
            posX += Math.cos(ang) * moveAmount;
            posY += Math.sin(ang) * moveAmount;
            chl += moveAmount / msl;
        }
        if (vfrb > 0) {
            for (int pointIndex = pts.size() - 1; pointIndex >= 0; pointIndex--) {
                point = pts.get(pointIndex);
                if (point.dying) {
                    point.da += 0.0015F * vfrb;
                    if (point.da > 1) {
                        pts.remove(pointIndex);
                        point.dying = false;
                    }
                }
                if (point.eiu > 0) {
                    int fx = 0;
                    int fy = 0;
                    int cm = point.eiu - 1;
                    for (int qq = cm; qq >= 0; qq--) {
                        point.efs[qq] = (int) (point.ems[qq] == 2 ? point.efs[qq] + vfrb2 : point.efs[qq] + vfrb);
                        int h = point.efs[qq];
                        if (h >= SlytherClient.HFC) {
                            if (qq != cm) {
                                point.exs[qq] = point.exs[cm];
                                point.eys[qq] = point.eys[cm];
                                point.efs[qq] = point.efs[cm];
                                point.ems[qq] = point.ems[cm];
                            }
                            point.eiu--;
                            cm--;
                        } else {
                            fx += point.exs[qq] * SlytherClient.HFAS[h];
                            fy += point.eys[qq] * SlytherClient.HFAS[h];
                        }
                    }
                    point.fx = fx;
                    point.fy = fy;
                }
            }
        }
        float ex = (float) (Math.cos(eang) * pma);
        float ey = (float) (Math.sin(eang) * pma);
        if (rex < ex) {
            rex += vfr / 6.0F;
            if (rex > ex) {
                rex = ex;
            }
        }
        if (rey < ey) {
            rey += vfr / 6.0F;
            if (rey > ey) {
                rey = ey;
            }
        }
        if (rex > ex) {
            rex -= vfr / 6;
            if (rex < ex) {
                rex = ex;
            }
        }
        if (rey > ey) {
            rey -= vfr / 6;
            if (rey < ey) {
                rey = ey;
            }
        }
        if (vfrb > 0) {
            if (ftg > 0) {
                float h = vfrb;
                if (h > ftg) {
                    h = ftg;
                }
                ftg -= h;
                for (int i = 0; i < h; i++) {
                    fx = fxs[fpos];
                    fy = fys[fpos];
                    fchl = fchls[fpos];
                    fxs[fpos] = 0;
                    fys[fpos] = 0;
                    fchls[fpos] = 0;
                    fpos++;
                    if (fpos >= SlytherClient.RFC) {
                        fpos = 0;
                    }
                }
            } else if (ftg == 0) {
                ftg = -1;
                fx = 0;
                fy = 0;
                fchl = 0;
            }
            if (fatg > 0) {
                float h = vfrb;
                if (h > fatg) {
                    h = fatg;
                }
                fatg -= h;
                for (int qq = 0; qq < h; qq++) {
                    fa = fas[fapos];
                    fas[fapos] = 0;
                    fapos++;
                    if (fapos >= SlytherClient.AFC) {
                        fapos = 0;
                    }
                }
            } else if (fatg == 0) {
                fatg = -1;
                fa = 0;
            }
        }
        if (dead) {
            deadAmt += 0.02F * vfr;
            if (deadAmt >= 1.0F) {
                client.snakes.remove(this);
            }
        } else {
            if (aliveAmt != 1) {
                aliveAmt += 0.015F * vfr;
                if (aliveAmt > 1.0F) {
                    aliveAmt = 1.0F;
                }
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Integer) {
            return id == (Integer) object;
        } else if (object instanceof Short) {
            return id == (Short) object;
        } else if (object instanceof Snake) {
            return id == ((Snake) object).id;
        }
        return false;
    }
}