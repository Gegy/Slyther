package net.gegy1000.slyther.game.entity;

import net.gegy1000.slyther.game.Game;

public class SnakePoint {
    public Game<?, ?> game;
    public float posX;
    public float posY;
    public float prevPosX;
    public float prevPosY;
    public float fx;
    public float fy;
    public float prevFx;
    public float prevFy;
    public float deltaX;
    public float deltaY;
    public boolean dying;
    public float deathAnimation;
    public int eiu;
    public int[] efs = new int[128];
    public float[] exs = new float[128];
    public float[] eys = new float[128];
    public float[] ems = new float[128];

    public SnakePoint(Game<?, ?> game, float posX, float posY) {
        this.game = game;
        this.posX = posX;
        this.posY = posY;
        prevPosX = posX;
        prevPosY = posY;
    }

    public void update() {
        prevPosX = posX;
        prevPosY = posY;
        prevFx = fx;
        prevFy = fy;
    }

    public float getRenderX(double frameDelta) {
        return (float) (prevPosX + frameDelta * (posX - prevPosX));
    }

    public float getRenderY(double frameDelta) {
        return (float) (prevPosY + frameDelta * (posY - prevPosY));
    }

    public float getRenderFX(double frameDelta) {
        return (float) (prevFx + frameDelta * (fx - prevFx));
    }

    public float getRenderFY(double frameDelta) {
        return (float) (prevFy + frameDelta * (fy - prevFy));
    }

    public boolean shouldTrack(Sector sector) {
        int sectorSize = game.getSectorSize();
        return (int) (posX / sectorSize) == sector.posX && (int) (posY / sectorSize) == sector.posY;
    }
}
