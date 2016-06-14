package net.gegy1000.slyther.client.game.entity;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Skin;
import net.gegy1000.slyther.game.entity.Snake;
import net.gegy1000.slyther.game.entity.SnakePoint;

import java.util.List;

public class ClientSnake extends Snake<SlytherClient> {
    public ClientSnake(SlytherClient game, String name, int id, float posX, float posY, Skin skin, float angle, List<SnakePoint> points) {
        super(game, name, id, posX, posY, skin, angle, points);
    }

    @Override
    public boolean update(float delta, float lastDelta, float lastDelta2) {
        for (SnakePoint point : points) {
            point.update();
        }
        prevAngle = angle;
        prevFx = fx;
        prevFy = fy;
        float turnSpeed = game.getBaseSnakeTurnSpeed() * delta * scaleTurnMultiplier * speedTurnMultiplier;
        float moveAmount = speed * delta / 4;
        if (moveAmount > msl) {
            moveAmount = msl;
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
            angle %= SlytherClient.PI_2;
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
            angle %= SlytherClient.PI_2;
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
                    for (int i = cm; i >= 0; i--) {
                        point.efs[i] = (int) (point.ems[i] == 2 ? point.efs[i] + lastDelta : point.efs[i] + lastDelta);
                        int h = point.efs[i];
                        if (h >= SlytherClient.HFC) {
                            if (i != cm) {
                                point.exs[i] = point.exs[cm];
                                point.eys[i] = point.eys[cm];
                                point.efs[i] = point.efs[cm];
                                point.ems[i] = point.ems[cm];
                            }
                            point.eiu--;
                            cm--;
                        } else {
                            fx += point.exs[i] * SlytherClient.HFAS[h];
                            fy += point.eys[i] * SlytherClient.HFAS[h];
                        }
                    }
                    point.fx = fx;
                    point.fy = fy;
                    point.prevFx = fx;
                    point.prevFy = fy;
                }
            }
        }
        float eyeX = (float) (Math.cos(eyeAngle) * pma);
        float eyeY = (float) (Math.sin(eyeAngle) * pma);
        if (relativeEyeX < eyeX) {
            relativeEyeX += delta / 6.0F;
            if (relativeEyeX > eyeX) {
                relativeEyeX = eyeX;
            }
        }
        if (relativeEyeY < eyeY) {
            relativeEyeY += delta / 6.0F;
            if (relativeEyeY > eyeY) {
                relativeEyeY = eyeY;
            }
        }
        if (relativeEyeX > eyeX) {
            relativeEyeX -= delta / 6;
            if (relativeEyeX < eyeX) {
                relativeEyeX = eyeX;
            }
        }
        if (relativeEyeY > eyeY) {
            relativeEyeY -= delta / 6;
            if (relativeEyeY < eyeY) {
                relativeEyeY = eyeY;
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
            if (foodAnglesToGo > 0) {
                float amount = lastDelta;
                if (amount > foodAnglesToGo) {
                    amount = foodAnglesToGo;
                }
                foodAnglesToGo -= amount;
                for (int i = 0; i < amount; i++) {
                    foodAngle = foodAngles[foodAngleIndex];
                    foodAngles[foodAngleIndex] = 0;
                    foodAngleIndex++;
                    if (foodAngleIndex >= SlytherClient.AFC) {
                        foodAngleIndex = 0;
                    }
                }
            } else if (foodAnglesToGo == 0) {
                foodAnglesToGo = -1;
                foodAngle = 0;
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
        prevAngle = getAngleForInterpolation(angle, prevAngle);
        return false;
    }
}
