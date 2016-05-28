package net.gegy1000.slyther.game.entity;

import net.gegy1000.slyther.game.Game;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public abstract class Entity {
    public Game game;
    public float posX;
    public float posY;

    public Entity(Game game, float posX, float posY) {
        this.game = game;
        this.posX = posX;
        this.posY = posY;
    }

    public abstract boolean shouldTrack(ConnectedClient client);

    public abstract void startTracking(ConnectedClient tracker);
    public abstract void stopTracking(ConnectedClient tracker);

    public abstract void updateServer();

    public abstract void updateClient(float vfr, float vfrb, float vfrb2);

    public void addChildren() {
    }

    public void removeChildren() {
    }
}
