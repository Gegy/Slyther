package net.gegy1000.slyther.game.entity;

import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.Game;
import net.gegy1000.slyther.server.ConnectedClient;

public abstract class Food<GME extends Game<?, ?>> extends Entity<GME> {
    public int id;
    public Color color;
    public float size;
    public boolean eaten;
    public int sectorX;
    public int sectorY;
    public boolean isNatural;
    public Snake eater;

    public float lrrad;
    public float fade;
    public float gfr;
    public float gr;
    public float wantedSpeed;
    public float eatenFr;
    public float renderX;
    public float renderY;
    public int fadeSpeed;
    public float rad;

    public Food(GME game, int posX, int posY, float size, boolean isNatural, Color color) {
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
        fadeSpeed = isNatural ? 2 : 1;
        rad = 0.00001F;
        lrrad = rad;
        gfr = (int) (64.0F * Math.random());
        gr = 0.64F + 0.1F * this.size;
        wantedSpeed = (float) (0.0225F * (2.0F * Math.random() - 1.0F));
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Food && id == ((Food) object).id;
    }

    @Override
    public void startTracking(ConnectedClient tracker) {
    }

    @Override
    public void stopTracking(ConnectedClient tracker) {
    }

    @Override
    public float getRenderX(double frameDelta) {
        return (float) (prevPosX + frameDelta * (renderX - prevPosX));
    }

    @Override
    public float getRenderY(double frameDelta) {
        return (float) (prevPosY + frameDelta * (renderY - prevPosY));
    }

    @Override
    public boolean canMove() {
        return false;
    }
}
