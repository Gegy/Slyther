package net.gegy1000.slyther.game.entity;

import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.Game;
import net.gegy1000.slyther.network.message.server.MessageNewPrey;
import net.gegy1000.slyther.server.ConnectedClient;

public abstract class Prey<GME extends Game<?, ?, ?, ?, ?, ?>> extends Entity<GME> {
    public int id;
    public float size;
    public Color color;
    public int turningDirection;
    public float wantedAngle;
    public float angle;
    public float speed;
    public boolean eaten;
    public Snake eater;

    public Prey(GME game, int id, float posX, float posY, float size, Color color, int turningDirection, float wantedAngle, float angle, float speed) {
        super(game, posX, posY);
        this.id = id;
        this.size = size;
        this.color = color;
        this.turningDirection = turningDirection;
        this.wantedAngle = wantedAngle;
        this.angle = angle;
        this.speed = speed;
    }

    @Override
    public void startTracking(ConnectedClient tracker) {
        tracker.send(new MessageNewPrey(this));
    }

    @Override
    public void stopTracking(ConnectedClient tracker) {
        tracker.send(new MessageNewPrey(this));
    }
}
