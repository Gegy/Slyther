package net.gegy1000.slyther.game.entity;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.Game;
import net.gegy1000.slyther.network.message.server.MessageNewPrey;
import net.gegy1000.slyther.server.ConnectedClient;

public abstract class Prey<GME extends Game<?, ?>> extends Entity<GME> {
    public int id;
    public float size;
    public Color color;
    public int turningDirection;
    public float wantedAngle;
    public float angle;
    public float speed;
    public boolean eaten;
    public Snake eater;

    public float gr;
    public float fr;
    public int gfr;
    public float[] fxs;
    public float[] fys;
    public int fpos;
    public int ftg;
    public float fx;
    public float fy;
    public float prevFx;
    public float prevFy;
    public float rad;
    public float eatenFR;

    public float renderX;
    public float renderY;

    public Prey(GME game, int id, float posX, float posY, float size, Color color, int turningDirection, float wantedAngle, float angle, float speed) {
        super(game, posX, posY);
        this.id = id;
        this.size = size;
        this.color = color;
        this.turningDirection = turningDirection;
        this.wantedAngle = wantedAngle;
        this.angle = angle;
        this.speed = speed;

        rad = 0.00001F;
        gfr = (int) (64 * Math.random());
        fxs = new float[SlytherClient.RFC];
        fys = new float[SlytherClient.RFC];
    }

    public float getRenderFX(double frameDelta) {
        return (float) (prevFx + frameDelta * (fx - prevFx));
    }

    public float getRenderFY(double frameDelta) {
        return (float) (prevFy + frameDelta * (fy - prevFy));
    }

    @Override
    public void startTracking(ConnectedClient tracker) {
        tracker.send(new MessageNewPrey(this));
    }

    @Override
    public void stopTracking(ConnectedClient tracker) {
        tracker.send(new MessageNewPrey(this));
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
        return true;
    }
}