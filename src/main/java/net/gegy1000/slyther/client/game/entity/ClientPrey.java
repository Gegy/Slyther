package net.gegy1000.slyther.client.game.entity;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.entity.Prey;

public class ClientPrey extends Prey<SlytherClient> {
    public ClientPrey(SlytherClient game, int id, float posX, float posY, float size, Color color, int turningDirection, float wantedAngle, float angle, float speed) {
        super(game, id, posX, posY, size, color, turningDirection, wantedAngle, angle, speed);
    }

    @Override
    public boolean update(float delta, float lastDelta, float lastDelta2) {
        prevFx = fx;
        prevFy = fy;
        prevPosX = renderX;
        prevPosY = renderY;
        float turnSpeed = game.getBasePreyTurnSpeed() * delta;
        float moveAmount = speed * delta / 4;
        if (lastDelta > 0) {
            if (ftg > 0) {
                float h = lastDelta;
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
        if (turningDirection == 1) {
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
                turningDirection = 0;
            }
        } else if (turningDirection == 2) {
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
                turningDirection = 0;
            }
        } else {
            angle = wantedAngle;
        }
        posX += Math.cos(angle) * moveAmount;
        posY += Math.sin(angle) * moveAmount;
        gfr += delta * gr;
        renderX = posX;
        renderY = posY;
        if (eaten) {
            if (eater != null) {
                float timer = (float) Math.pow(eatenFR, 2);
                renderX = (float) (posX + (eater.posX + eater.fx + Math.cos(eater.angle + eater.foodAngle) * (43 - 24 * timer) * (1 - timer) - posX) * timer);
                renderY = (float) (posY + (eater.posY + eater.fy + Math.sin(eater.angle + eater.foodAngle) * (43 - 24 * timer) * (1 - timer) - posY) * timer);
            }
            if (fr != 1.5F) {
                fr += delta / 150.0F;
                if (fr >= 1.5F) {
                    fr = 1.5F;
                }
            }
            eatenFR += delta / 47.0F;
            gfr += delta;
            if (eatenFR >= 1 || eater == null) {
                return true;
            } else {
                rad = (float) (1 - Math.pow(eatenFR, 3));
            }
        } else {
            if (fr != 1.0F) {
                fr += delta / 150.0F;
                if (fr >= 1.0F) {
                    fr = 1.0F;
                    rad = 1.0F;
                } else {
                    rad = (float) (0.5F * (1.0F - Math.cos(Math.PI * fr)));
                    rad += 0.66F * (0.5F * (1.0F - Math.cos(Math.PI * rad)) - rad);
                }
            }
        }
        return false;
    }
}
