package net.gegy1000.slyther.client.game.entity;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Skin;
import net.gegy1000.slyther.game.SkinColor;
import net.gegy1000.slyther.game.SkinDetails;
import net.gegy1000.slyther.game.SkinHandler;
import net.gegy1000.slyther.game.entity.Snake;
import net.gegy1000.slyther.game.entity.SnakePoint;
import org.lwjgl.input.Mouse;

import java.util.List;

public class ClientSnake extends Snake<SlytherClient> {
    public boolean antenna;
    public boolean oneEye;
    public float swell;
    public float atba;
    public int atc1;
    public int atc2;
    public boolean atwg;
    public float atia;
    public boolean antennaBottomRotate;
    public float[] antennaX;
    public float[] antennaY;
    public float[] antennaVelocityX;
    public float[] antennaVelocityY;
    public float[] atax;
    public float[] atay;
    public float antennaScale = 1.0F;
    public String faceTexture;
    public boolean isInView;
    public boolean antennaShown;
    public String antennaTexture;
    public SkinColor[] pattern;
    public SkinDetails skinDetails;
    public SkinColor color;
    public int er;
    public float pr;
    public float pma;
    public int eyeColor;
    public float eca;
    public int ppa;
    public int ppc;
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

    public ClientSnake(SlytherClient game, String name, int id, float posX, float posY, Skin skin, float angle, List<SnakePoint> points) {
        super(game, name, id, posX, posY, angle, points);
        setSkin(skin);
        fxs = new float[SlytherClient.RFC];
        fys = new float[SlytherClient.RFC];
        fchls = new float[SlytherClient.RFC];
        fas = new float[SlytherClient.AFC];
        ehang = angle;
        wehang = angle;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
        er = 6;
        pr = 3.5F;
        pma = 2.3F;
        eyeColor = 0xFFFFFF;
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
            antennaBottomRotate = details.abrot;
            int antennaLength = details.antennaLength;
            antennaX = new float[antennaLength];
            antennaY = new float[antennaLength];
            antennaVelocityX = new float[antennaLength];
            antennaVelocityY = new float[antennaLength];
            atax = new float[antennaLength];
            atay = new float[antennaLength];
            for (int i = 0; i < antennaLength; i++) {
                antennaX[i] = posX;
                antennaY[i] = posY;
            }
            eyeColor = details.eyeColor;
            eca = details.eca;
            oneEye = details.oneEye;
            pma = details.pma;
            swell = details.swell;
            antennaTexture = details.antennaTexture;
            antennaScale = details.antennaScale;
            pattern = details.pattern;
            skinDetails = details;
            faceTexture = details.faceTexture;
        }

        this.pattern = pattern;
        color = pattern[0];
    }

    @Override
    public boolean update(float delta, float lastDelta, float lastDelta2) {
        float turnSpeed = game.getMamu() * delta * scaleTurnMultiplier * speedTurnMultiplier;
        float moveAmount = speed * delta / 4;
        if (moveAmount > msl) {
            moveAmount = msl;
        }
        if (game.allowUserInput) {
            if (this == game.player) {
                boolean prev = mouseDown;
                mouseDown = Mouse.isButtonDown(0) || Mouse.isButtonDown(1);
                if (prev != mouseDown) {
                    wasMouseDown = prev;
                }
            }
        }
        if (!dead) {
            if (tsp != speed) {
                if (tsp < speed) {
                    tsp += 0.3F * delta;
                    if (tsp > speed) {
                        tsp = speed;
                    }
                } else {
                    tsp -= 0.3F * delta;
                    if (tsp < speed) {
                        tsp = speed;
                    }
                }
            }
            if (tsp > accelleratingSpeed) {
                sfr += (tsp - accelleratingSpeed) * delta * 0.021F;
            }
            if (fltg > 0) {
                float h = lastDelta;
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
            cfl = totalLength + fl;
        }
        if (turnDirection == 1) {
            angle -= turnSpeed;
            if (angle < 0 || angle >= SlytherClient.PI_2) {
                angle %= SlytherClient.PI_2;
            }
            if (angle < 0) {
                angle += SlytherClient.PI_2;
            }
            float h = (float) ((wantedAngle - angle) % SlytherClient.PI_2);
            if (h < 0) {
                h += SlytherClient.PI_2;
            }
            if (h > Math.PI) {
                h -= SlytherClient.PI_2;
            }
            if (h > 0) {
                angle = wantedAngle;
                turnDirection = 0;
            }
        } else if (turnDirection == 2) {
            angle += turnSpeed;
            if (angle < 0 || angle >= SlytherClient.PI_2) {
                angle %= SlytherClient.PI_2;
            }
            if (angle < 0) {
                angle += SlytherClient.PI_2;
            }
            float h = (float) ((wantedAngle - angle) % SlytherClient.PI_2);
            if (h < 0) {
                h += SlytherClient.PI_2;
            }
            if (h > Math.PI) {
                h -= SlytherClient.PI_2;
            }
            if (h < 0) {
                angle = wantedAngle;
                turnDirection = 0;
            }
        } else {
            angle = wantedAngle;
        }
        if (ehl != 1) {
            ehl += 0.03F * delta;
            if (ehl >= 1) {
                ehl = 1;
            }
        }
        SnakePoint point = points.get(points.size() - 1);
        if (point != null) {
            wehang = (float) Math.atan2(posY + fy - point.posY - point.fy + point.deltaY * (1.0F - ehl), posX + fx - point.posX - point.fx + point.deltaX * (1.0F - ehl));
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
            ehang -= 0.1F * delta;
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
            ehang += 0.1F * delta;
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
            posX += Math.cos(angle) * moveAmount;
            posY += Math.sin(angle) * moveAmount;
            chl += moveAmount / msl;
        }
        if (lastDelta > 0) {
            for (int pointIndex = points.size() - 1; pointIndex >= 0; pointIndex--) {
                point = points.get(pointIndex);
                if (point.dying) {
                    point.deathAnimation += 0.0015F * lastDelta;
                    if (point.deathAnimation > 1) {
                        points.remove(pointIndex);
                        point.dying = false;
                    }
                }
                if (point.eiu > 0) {
                    int fx = 0;
                    int fy = 0;
                    int cm = point.eiu - 1;
                    for (int qq = cm; qq >= 0; qq--) {
                        point.efs[qq] = (int) (point.ems[qq] == 2 ? point.efs[qq] + lastDelta : point.efs[qq] + lastDelta);
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
        float ex = (float) (Math.cos(eyeAngle) * pma);
        float ey = (float) (Math.sin(eyeAngle) * pma);
        if (rex < ex) {
            rex += delta / 6.0F;
            if (rex > ex) {
                rex = ex;
            }
        }
        if (rey < ey) {
            rey += delta / 6.0F;
            if (rey > ey) {
                rey = ey;
            }
        }
        if (rex > ex) {
            rex -= delta / 6;
            if (rex < ex) {
                rex = ex;
            }
        }
        if (rey > ey) {
            rey -= delta / 6;
            if (rey < ey) {
                rey = ey;
            }
        }
        if (lastDelta > 0) {
            if (ftg > 0) {
                float h = lastDelta;
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
                float h = lastDelta;
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
            deadAmt += delta * 0.02F;
            if (deadAmt >= 1.0F) {
                game.removeEntity(this);
            }
        } else {
            if (aliveAmt != 1) {
                aliveAmt += delta * 0.015F;
                if (aliveAmt > 1.0F) {
                    aliveAmt = 1.0F;
                }
            }
        }
        return false;
    }
}
