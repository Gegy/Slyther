package net.gegy1000.slyther.game.entity;

import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.Game;
import net.gegy1000.slyther.network.message.server.MessageNewFood;
import net.gegy1000.slyther.network.message.server.MessageRemoveFood;
import net.gegy1000.slyther.server.ConnectedClient;

public class Food extends Entity {
    public int id;
    public float renderX;
    public float renderY;
    public int rsp;
    public Color color;
    public float rad;
    public float size;
    public float lrrad;
    public float fr;
    public float gfr;
    public float gr;
    public float wsp;
    public float eatenFr;
    public boolean eaten;
    public Snake eater;
    public int sectorX;
    public int sectorY;
    public boolean isNatural;

    public Food(Game game, int posX, int posY, float size, boolean isNatural, Color color) {
        super(game, posX, posY);
        this.posX = posX;
        this.posY = posY;
        this.isNatural = isNatural;
        this.color = color;
        this.size = size;
        id = posY * game.getGameRadius() * 3 + posX;
        sectorX = (int) Math.floor(posX / game.getSectorSize());
        sectorY = (int) Math.floor(posY / game.getSectorSize());
        renderX = posX;
        renderY = posY;
        rsp = isNatural ? 2 : 1;
        rad = 0.00001F;
        lrrad = rad;
        gfr = (int) (64.0F * Math.random());
        gr = 0.64F + 0.1F * this.size;
        wsp = (float) (0.0225F * (2.0F * Math.random() - 1.0F));
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Food && id == ((Food) object).id;
    }

    @Override
    public void startTracking(ConnectedClient tracker) {
        tracker.send(new MessageNewFood(this));
    }

    @Override
    public void stopTracking(ConnectedClient tracker) {
        tracker.send(new MessageRemoveFood(this));
    }

    @Override
    public void updateServer() {

    }

    @Override
    public void updateClient(float vfr, float vfrb, float vfrb2) {
        gfr += vfr * gr;
        if (eaten) {
            eatenFr += vfr / 41.0F;
            if (eatenFr >= 1.0F || eater == null) {
                game.removeEntity(this);
            } else {
                float eaterFrSq = eatenFr * eatenFr;
                rad = lrrad * (1.0F - eatenFr * eaterFrSq);
                renderX = (int) (posX + (eater.posX + eater.fx + Math.cos(eater.angle + eater.fa) * (43.0F - 24.0F * eaterFrSq) * (1.0F - eaterFrSq) - posX) * eaterFrSq);
                renderY = (int) (posY + (eater.posY + eater.fy + Math.cos(eater.angle + eater.fa) * (43.0F - 24.0F * eaterFrSq) * (1.0F - eaterFrSq) - posY) * eaterFrSq);
                renderX += Math.cos(wsp * gfr) * (1.0F - eatenFr) * 6.0F;
                renderY += Math.sin(wsp * gfr) * (1.0F - eatenFr) * 6.0F;
            }
        } else {
            if (fr != 1.0F) {
                fr += rsp * vfr / 150.0F;
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
    }
}
