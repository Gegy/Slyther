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
    public int atba;
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
    public float fsp;
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

    public Snake(SlytherClient client, int id, float x, float y, Skin skin, float angle, List<SnakePoint> points) {
        this.client = client;
        this.id = id;
        this.posX = x;
        this.posY = y;
        this.setSkin(skin);
        this.na = 1;
        this.rr = (int) Math.min(255, this.cv.red + Math.floor(20.0F * Math.random()));
        this.gg = (int) Math.min(255, this.cv.green + Math.floor(20.0F * Math.random()));
        this.bb = (int) Math.min(255, this.cv.blue + Math.floor(20.0F * Math.random()));
        this.cs = ((rr & 0xFF) << 16) | ((gg & 0xFF) << 8) | (bb & 0xFF);
        this.cs04 = (int) ((Math.min(255, Math.max(0, Math.round(0.4 * rr))) & 0xFF) << 16 | Math.min(255, Math.max(0, Math.round(0.4 * gg) & 0xFF) << 8) | Math.min(255, Math.max(0, Math.round(0.4 * bb) & 0xFF)));
        this.csw = (int) ((Math.min(255, Math.max(0, Math.round(0.5 * rr))) & 0xFF) << 16 | Math.min(255, Math.max(0, Math.round(0.5 * gg) & 0xFF) << 8) | Math.min(255, Math.max(0, Math.round(0.5 * bb) & 0xFF)));
        this.sc = 1.0F;
        this.ssp = client.NSP1 + client.NSP2 * this.sc;
        this.fsp = this.ssp + 0.1F;
        this.msp = client.NSP3;
        this.fxs = new float[SlytherClient.RFC];
        this.fys = new float[SlytherClient.RFC];
        this.fchls = new float[SlytherClient.RFC];
        this.fas = new float[SlytherClient.AFC];
        this.ehang = angle;
        this.wehang = angle;
        this.ehl = 1;
        this.msl = 42;
        this.ang = angle;
        this.eang = angle;
        this.wang = angle;
        this.sp = 2;

        if (points != null) {
            this.lnp = points.get(points.size() - 1);
            this.pts = points;
            this.sct = points.size();
            if (points.get(0).dying) {
                this.sct--;
            }
        } else {
            this.pts = new ArrayList<>();
        }

        this.fls = new float[SlytherClient.LFC];
        this.tl = this.sct + this.fam;
        this.cfl = this.tl;
        this.scang = 1;
        this.deadAmt = 0;
        this.aliveAmt = 0;
    }

    public void setSkin(Skin skin) {
        this.rcv = skin;
        this.er = 6;
        this.pr = 3.5F;
        this.pma = 2.3F;
        this.ec = 0xFFFFFF;
        this.eca = 0.75F;
        this.ppa = 1;

        SkinDetails details = SkinHandler.INSTANCE.getDetails(skin);

        SkinColor[] pattern = new SkinColor[] { SkinColor.values()[skin.ordinal() % SkinColor.values().length] };

        if (details != null) {
            this.antenna = details.hasAntenna;
            this.atc1 = details.antennaPrimaryColor;
            this.atc2 = details.antennaSecondaryColor;
            this.atwg = details.atwg;
            this.atia = details.atia;
            this.abrot = details.abrot;
            int antennaLength = details.antennaLength;
            this.atx = new float[antennaLength];
            this.aty = new float[antennaLength];
            this.atvx = new float[antennaLength];
            this.atvy = new float[antennaLength];
            this.atax = new float[antennaLength];
            this.atay = new float[antennaLength];
            for (int i = 0; i < antennaLength; i++) {
                this.atx[i] = this.posX;
                this.aty[i] = this.posY;
            }
            this.blbx = details.blbx;
            this.blby = details.blby;
            this.blbw = details.blbw;
            this.blbh = details.blbh;
            this.bsc = details.bsc;
            this.blba = details.blba;
            this.ec = details.eyeColor;
            this.eca = details.eca;
            this.oneEye = details.oneEye;
            this.ebiw = details.ebiw;
            this.ebih = details.ebih;
            this.ebisz = details.ebisz;
            this.epiw = details.epiw;
            this.epih = details.epih;
            this.episz = details.episz;
            this.pma = details.pma;
            this.swell = details.swell;

            pattern = details.pattern;
        }

        this.rbcs = pattern;
        this.cv = pattern[0];
    }

    public void snl() {
        double d = this.tl;
        this.tl = this.sct + this.fam;
        d = this.tl - d;
        int b = this.flpos;
        for (int i = 0; i < SlytherClient.LFC; i++) {
            this.fls[b] -= d * SlytherClient.LFAS[i];
            b++;
            if (b >= SlytherClient.LFC) {
                b = 0;
            }
        }
        this.fl = this.fls[this.flpos];
        this.fltg = SlytherClient.LFC;
        if (this == this.client.player) {
            this.client.wumsts = true;
        }
    }

    public void update(float vfr, float vfrb, float vfrb2) {
        float c = client.MAMU * vfr * this.scang * this.spang;
        float moveAmount = this.sp * vfr / 4;
        if (moveAmount > this.msl) {
            moveAmount = this.msl;
        }
        if (this == this.client.player) {
            boolean prev = this.md;
            this.md = Mouse.isButtonDown(0) || Mouse.isButtonDown(1);
            if (prev != this.md) {
                this.prevMd = prev;
            }
        }
        if (!this.dead) {
            if (this.tsp != this.sp) {
                if (this.tsp < this.sp) {
                    this.tsp += 0.3F * vfr;
                    if (this.tsp > this.sp) {
                        this.tsp = this.sp;
                    }
                } else {
                    this.tsp -= 0.3F * vfr;
                    if (this.tsp < this.sp) {
                        this.tsp = this.sp;
                    }
                }
            }
            if (this.tsp > this.fsp) {
                this.sfr += (this.tsp - this.fsp) * vfr * 0.021F;
            }
            if (this.fltg > 0) {
                float h = vfrb;
                if (h > this.fltg) {
                    h = this.fltg;
                }
                this.fltg -= h;
                for (int i = 0; i < h; i++) {
                    this.fl = this.fls[this.flpos];
                    this.fls[this.flpos] = 0;
                    this.flpos++;
                    if (this.flpos >= SlytherClient.LFC) {
                        this.flpos = 0;
                    }
                }
            } else {
                if (this.fltg == 0) {
                    this.fltg = -1;
                    this.fl = 0;
                }
            }
            this.cfl = this.tl + this.fl;
        }
        if (this.dir == 1) {
            this.ang -= c;
            if (this.ang < 0 || this.ang >= SlytherClient.PI_2) {
                this.ang %= SlytherClient.PI_2;
            }
            if (this.ang < 0) {
                this.ang += SlytherClient.PI_2;
            }
            float h = (float) ((this.wang - this.ang) % SlytherClient.PI_2);
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
            float h = (float) ((this.wang - this.ang) % SlytherClient.PI_2);
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
        if (this.ehl != 1) {
            this.ehl += 0.03F * vfr;
            if (this.ehl >= 1) {
                this.ehl = 1;
            }
        }
        SnakePoint point = this.pts.get(this.pts.size() - 1);
        if (point != null) {
            wehang = (float) Math.atan2(this.posY + this.fy - point.posY - point.fy + point.eby * (1.0F - this.ehl), this.posX + this.fx - point.posX - point.fx + point.ebx * (1.0F - this.ehl));
        }
        if (!this.dead) {
            if (this.ehang != this.wehang) {
                float h = (float) ((this.wehang - this.ehang) % SlytherClient.PI_2);
                if (h < 0) {
                    h += SlytherClient.PI_2;
                }
                if (h > Math.PI) {
                    h -= SlytherClient.PI_2;
                }
                if (h < 0) {
                    this.edir = 1;
                } else {
                    if (h > 0) {
                        this.edir = 2;
                    }
                }
            }
        }
        if (this.edir == 1) {
            this.ehang -= 0.1F * vfr;
            if (this.ehang < 0 || this.ehang >= SlytherClient.PI_2) {
                this.ehang %= SlytherClient.PI_2;
            }
            if (this.ehang < 0) {
                this.ehang += SlytherClient.PI_2;
            }
            float h = (float) ((this.wehang - this.ehang) % SlytherClient.PI_2);
            if (h < 0) {
                h += SlytherClient.PI_2;
            }
            if (h > Math.PI) {
                h -= SlytherClient.PI_2;
            }
            if (h > 0) {
                this.ehang = this.wehang;
                this.edir = 0;
            }
        } else if (this.edir == 2) {
            this.ehang += 0.1F * vfr;
            if (this.ehang < 0 || this.ehang >= SlytherClient.PI_2) {
                this.ehang %= SlytherClient.PI_2;
            }
            if (this.ehang < 0) {
                this.ehang += SlytherClient.PI_2;
            }
            float h = (float) ((this.wehang - this.ehang) % SlytherClient.PI_2);
            if (h < 0) {
                h += SlytherClient.PI_2;
            }
            if (h > Math.PI) {
                h -= SlytherClient.PI_2;
            }
            if (h < 0) {
                this.ehang = this.wehang;
                this.edir = 0;
            }
        }
        if (!this.dead) {
            this.posX += Math.cos(this.ang) * moveAmount;
            this.posY += Math.sin(this.ang) * moveAmount;
            this.chl += moveAmount / this.msl;
        }
        if (vfrb > 0) {
            for (int pointIndex = this.pts.size() - 1; pointIndex >= 0; pointIndex--) {
                point = this.pts.get(pointIndex);
                if (point.dying) {
                    point.da += 0.0015F * vfrb;
                    if (point.da > 1) {
                        this.pts.remove(pointIndex);
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
        float ex = (float) (Math.cos(this.eang) * this.pma);
        float ey = (float) (Math.sin(this.eang) * this.pma);
        if (this.rex < ex) {
            this.rex += vfr / 6.0F;
            if (this.rex > ex) {
                this.rex = ex;
            }
        }
        if (this.rey < ey) {
            this.rey += vfr / 6.0F;
            if (this.rey > ey) {
                this.rey = ey;
            }
        }
        if (this.rex > ex) {
            this.rex -= vfr / 6;
            if (this.rex < ex) {
                this.rex = ex;
            }
        }
        if (this.rey > ey) {
            this.rey -= vfr / 6;
            if (this.rey < ey) {
                this.rey = ey;
            }
        }
        if (vfrb > 0) {
            if (this.ftg > 0) {
                float h = vfrb;
                if (h > this.ftg) {
                    h = this.ftg;
                }
                this.ftg -= h;
                for (int i = 0; i < h; i++) {
                    this.fx = this.fxs[this.fpos];
                    this.fy = this.fys[this.fpos];
                    this.fchl = this.fchls[this.fpos];
                    this.fxs[this.fpos] = 0;
                    this.fys[this.fpos] = 0;
                    this.fchls[this.fpos] = 0;
                    this.fpos++;
                    if (this.fpos >= SlytherClient.RFC) {
                        this.fpos = 0;
                    }
                }
            } else if (this.ftg == 0) {
                this.ftg = -1;
                this.fx = 0;
                this.fy = 0;
                this.fchl = 0;
            }
            if (this.fatg > 0) {
                float h = vfrb;
                if (h > this.fatg) {
                    h = this.fatg;
                }
                this.fatg -= h;
                for (int qq = 0; qq < h; qq++) {
                    this.fa = this.fas[this.fapos];
                    this.fas[this.fapos] = 0;
                    this.fapos++;
                    if (this.fapos >= SlytherClient.AFC) {
                        this.fapos = 0;
                    }
                }
            } else if (this.fatg == 0) {
                this.fatg = -1;
                this.fa = 0;
            }
        }
        if (this.dead) {
            this.deadAmt += 0.02F * vfr;
            if (this.deadAmt >= 1.0F) {
                client.snakes.remove(this);
            }
        } else {
            if (this.aliveAmt != 1) {
                this.aliveAmt += 0.015F * vfr;
                if (this.aliveAmt > 1.0F) {
                    this.aliveAmt = 1.0F;
                }
            }
        }
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
}