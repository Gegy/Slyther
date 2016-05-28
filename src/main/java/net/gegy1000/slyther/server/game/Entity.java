package net.gegy1000.slyther.server.game;

import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public abstract class Entity {
    public SlytherServer server;
    public float posX;
    public float posY;

    public Entity(SlytherServer server, float posX, float posY) {
        this.server = server;
        this.posX = posX;
        this.posY = posY;
    }

    public abstract boolean shouldTrack(ConnectedClient client);

    public abstract void startTracking(ConnectedClient tracker);
    public abstract void stopTracking(ConnectedClient tracker);
}
