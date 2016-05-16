package net.gegy1000.slyther.game;

import net.gegy1000.slyther.client.SlytherClient;

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
    public boolean anntenna;
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
    public int[] rbcs;
    public int cv;
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
    public float fam;
    public float ang;
    public float eang;
    public float wang;
    public float rex;
    public float rey;
    public float sp;
    public SnakePart lnp; // Tail Part or Head Part (Last part entry)
    public List<SnakePart> pts;
    public int sct;
    public int flpos;
    public float[] fls;
    public float fl;
    public int fltg;
    public float tl;
    public float cfl;
    public float scang;
    public float spang;
    public float deadAmt;
    public float aliveAmt;
    public boolean md;
    public boolean wmd;
    public boolean dead;
    public int dir;
    public int edir;
    public float sep;
    public float wsep;
    public boolean iiv;

    public Snake(SlytherClient client, int id, float x, float y, Skin skin, float angle, List<SnakePart> parts) {
        this.client = client;
        this.id = id;
        this.posX = x;
        this.posY = y;
        this.setSkin(skin);
        this.na = 1;
        SkinColor color = SkinColor.values()[this.cv];
        this.rr = (int) Math.min(255, color.red + Math.floor(20.0F * Math.random()));
        this.gg = (int) Math.min(255, color.green + Math.floor(20.0F * Math.random()));
        this.bb = (int) Math.min(255, color.blue + Math.floor(20.0F * Math.random()));
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

        if (parts != null) {
            this.lnp = parts.get(parts.size() - 1);
            this.pts = parts;
            this.sct = parts.size();
            if (parts.get(0).dying) {
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
        this.ppc = 0x000000;
        this.anntenna = false;
        this.oneEye = false;
        this.swell = 0;

        if (skin == Skin.ARCADE_GO) {
            this.anntenna = true;
            this.atba = 0;
            this.atc1 = 0x00688C;
            this.atc2 = 0x64C8E7;
            this.atwg = true;
            this.atia = 0.35F;
            this.abrot = false;
            int b = 8;
            this.atx = new float[b];
            this.aty = new float[b];
            this.atvx = new float[b];
            this.atvy = new float[b];
            this.atax = new float[b];
            this.atay = new float[b];
            for (int i = 0; i < b; i++) {
                this.atx[i] = this.posX;
                this.aty[i] = this.posY;
            }
            this.blbx = -10;
            this.blby = -10;
            this.blbw = 20;
            this.blbh = 20;
            this.bsc = 1;
            this.blba = 0.75F;
        } else if (skin == Skin.ORANGE_BLUE_STRIPE_HEAD_TAIL) {
            this.ec = 0xFF5609;
            this.eca = 1;
            this.anntenna = true;
            this.atba = 0;
            this.atc1 = 0x000000;
            this.atc2 = 0x5630D7;
            this.atia = 1;
            this.abrot = true;
            int b = 9;
            this.atx = new float[b];
            this.aty = new float[b];
            this.atvx = new float[b];
            this.atvy = new float[b];
            this.atax = new float[b];
            this.atay = new float[b];
            for (int i = 0; i < b; i++) {
                this.atx[i] = this.posX;
                this.aty[i] = this.posY;
            }
            this.blbx = -5;
            this.blby = -10;
            this.blbw = 20;
            this.blbh = 20;
            this.bsc = 1.6F;
            this.blba = 1;
        } else if (skin == Skin.GREEN_EYEBALL) {
            this.oneEye = true;
//            this.ebi = jsebi;
            this.ebiw = 64;
            this.ebih = 64;
            this.ebisz = 29;
//            this.epi = jsepi;
            this.epiw = 48;
            this.epih = 48;
            this.episz = 14;
            this.pma = 4;
            this.swell = 0.06F;
        }

        int[] pattern = SkinColorHandler.INSTANCE.getPattern(skin);

        if (pattern != null) {
            this.cv = pattern[0];
        } else {
            this.cv = skin.ordinal();
        }

        this.rbcs = pattern;
    }

    public void snl() {
        float f = this.tl;
        this.tl = this.sct + this.fam;
        f = this.tl - f;
        int b = this.flpos;
        for (int i = 0; i < SlytherClient.LFC; i++) {
            this.fls[b] -= f * SlytherClient.LFAS[i];
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
            if (this.ang < 0 || this.ang > SlytherClient.PI_2) {
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
            if (this.ang < 0 || this.ang > SlytherClient.PI_2) {
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
        SnakePart part = this.pts.get(this.pts.size() - 1);
        part.wehang = (float) Math.atan2(this.posY + this.fy - part.posY - part.fy + part.eby * (1 - this.ehl), this.posX + this.fx - part.posX - part.fx + part.ebx * (1 - this.ehl));
        if (!this.dead) {
            if (!(this.ehang == this.wehang)) {
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
        if (vfrb < 0) {
            for (int partIndex = this.pts.size() - 1; partIndex >= 0; partIndex--) {
                part = this.pts.get(partIndex);
                if (part.dying) {
                    part.da += 0.0015F * vfrb;
                    if (part.da > 1) {
                        this.pts.remove(partIndex);
                        part.dying = false;
                        client.deadpool.add(part);
                    }
                }
            }
            for (int partIndex = this.pts.size() - 1; partIndex >= 0; partIndex--) {
                part = this.pts.get(partIndex);
                if (part.eiu > 0) {
                    int fx = 0;
                    int fy = 0;
                    int cm = part.eiu - 1;
                    for (int qq = cm; qq >= 0; qq--) {
                        part.efs.set(qq, (int) (2 == part.ems.get(qq) ? part.efs.get(qq) + vfrb2 : part.efs.get(qq) + vfrb));
                        int h = part.efs.get(qq);
                        if (h >= SlytherClient.HFC) {
                            if (qq != cm) {
                                part.exs.set(qq, part.exs.get(cm));
                                part.eys.set(qq, part.eys.get(cm));
                                part.efs.set(qq, part.efs.get(cm));
                                part.ems.set(qq, part.ems.get(cm));
                            }
                            part.eiu--;
                            cm--;
                        } else {
                            fx += part.exs.get(qq) * SlytherClient.HFAS[h];
                            fy += part.eys.get(qq) * SlytherClient.HFAS[h];
                        }
                    }
                    part.fx = fx;
                    part.fy = fy;
                }
            }
        }
        float eX = (float) (Math.cos(this.eang) * this.pma);
        float eY = (float) (Math.sin(this.eang) * this.pma);
        if (this.rex < eX) {
            this.rex += vfr / 6.0F;
            if (this.rex > eX) {
                this.rex = eX;
            }
        }
        if (this.rey < eY) {
            this.rey += vfr / 6.0F;
            if (this.rey > eY) {
                this.rey = eY;
            }
        }
        if (this.rex > eX) {
            this.rex -= vfr / 6;
            if (this.rex <= eX) {
                this.rex = eX;
            }
        }
        if (this.rey > eY) {
            this.rey -= vfr / 6;
            if (this.rey <= eY) {
                this.rey = eY;
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
            } else {
                if (this.ftg == 0) {
                    this.ftg = -1;
                    this.fx = 0;
                    this.fy = 0;
                    this.fchl = 0;
                }
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
            } else {
                if (this.fatg == 0) {
                    this.fatg = -1;
                    this.fa = 0;
                }
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
                if (this.aliveAmt >= 1.0F) {
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