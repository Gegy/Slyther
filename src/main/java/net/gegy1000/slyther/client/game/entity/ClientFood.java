package net.gegy1000.slyther.client.game.entity;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.entity.Food;

public class ClientFood extends Food<SlytherClient> {
    public ClientFood(SlytherClient game, int posX, int posY, float size, boolean isNatural, Color color) {
        super(game, posX, posY, size, isNatural, color);
    }

    @Override
    public boolean update(float delta, float lastDelta, float lastDelta2) {
        gfr += delta * gr;
        if (eaten) {
            eatenFr += delta / 41.0F;
            if (eatenFr >= 1.0F || eater == null) {
                return true;
            } else {
                float eaterFrSq = eatenFr * eatenFr;
                rad = lrrad * (1.0F - eatenFr * eaterFrSq);
                renderX = (int) (posX + (eater.posX + eater.fx + Math.cos(eater.angle + eater.foodAngle) * (43.0F - 24.0F * eaterFrSq) * (1.0F - eaterFrSq) - posX) * eaterFrSq);
                renderY = (int) (posY + (eater.posY + eater.fy + Math.cos(eater.angle + eater.foodAngle) * (43.0F - 24.0F * eaterFrSq) * (1.0F - eaterFrSq) - posY) * eaterFrSq);
                renderX += Math.cos(wsp * gfr) * (1.0F - eatenFr) * 6.0F;
                renderY += Math.sin(wsp * gfr) * (1.0F - eatenFr) * 6.0F;
            }
        } else {
            if (fr != 1.0F) {
                fr += rsp * delta / 150.0F;
                if (fr >= 1.0F) {
                    fr = 1.0F;
                    rad = 1.0F;
                } else {
                    rad = (float) ((1.0F - Math.cos(Math.PI * fr)) * 0.5F);
                    rad += 0.66F * (0.5F * (1.0F - Math.cos(Math.PI * rad)) - rad);
                }
                lrrad = rad;
            }
            renderX = (int) (posX + 6.0F * Math.cos(wsp * gfr));
            renderY = (int) (posY + 6.0F * Math.sin(wsp * gfr));
        }
        return false;
    }
}
